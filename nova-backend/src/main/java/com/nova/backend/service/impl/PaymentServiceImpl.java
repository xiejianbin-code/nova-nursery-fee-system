package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.PaymentDTO;
import com.nova.backend.dto.PaymentQueryDTO;
import com.nova.backend.entity.BalanceAccount;
import com.nova.backend.entity.Child;
import com.nova.backend.entity.Payment;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.BalanceAccountMapper;
import com.nova.backend.mapper.ChildMapper;
import com.nova.backend.mapper.PaymentMapper;
import com.nova.backend.service.BalanceService;
import com.nova.backend.service.PaymentService;
import com.nova.backend.util.SnowflakeIdGenerator;
import com.nova.backend.enums.AccountTypeEnum;
import com.nova.backend.vo.PaymentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收费服务实现类
 *
 * @author Nova
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final ChildMapper childMapper;
    private final BalanceAccountMapper balanceAccountMapper;
    private final BalanceService balanceService;

    @Override
    public Page<PaymentVO> pageList(PaymentQueryDTO queryDTO) {
        Page<Payment> page = new Page<>(
                queryDTO.getPage() != null ? queryDTO.getPage() : 1,
                queryDTO.getSize() != null ? queryDTO.getSize() : 10
        );

        LambdaQueryWrapper<Payment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(queryDTO.getReceiptNo()), Payment::getReceiptNo, queryDTO.getReceiptNo())
                .eq(queryDTO.getChildId() != null, Payment::getChildId, queryDTO.getChildId())
                .eq(StringUtils.hasText(queryDTO.getPaymentType()), Payment::getPaymentType, queryDTO.getPaymentType())
                .eq(StringUtils.hasText(queryDTO.getPaymentMethod()), Payment::getPaymentMethod, queryDTO.getPaymentMethod())
                .eq(StringUtils.hasText(queryDTO.getStatus()), Payment::getStatus, queryDTO.getStatus());

        if (queryDTO.getPaymentDateBegin() != null) {
            queryWrapper.ge(Payment::getCreateTime, queryDTO.getPaymentDateBegin().atStartOfDay());
        }
        if (queryDTO.getPaymentDateEnd() != null) {
            queryWrapper.le(Payment::getCreateTime, queryDTO.getPaymentDateEnd().atTime(23, 59, 59));
        }

        queryWrapper.orderByDesc(Payment::getCreateTime);

        Page<Payment> result = paymentMapper.selectPage(page, queryWrapper);

        Page<PaymentVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(PaymentDTO dto) {
        Child child = childMapper.selectById(dto.getChildId());
        if (child == null) {
            throw new BusinessException("幼儿不存在");
        }

        Payment payment = new Payment();
        BeanUtils.copyProperties(dto, payment);
        payment.setReceiptNo(generateReceiptNo());
        payment.setStatus("VALID");
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.insert(payment);

        balanceService.addBalance(
                dto.getChildId(),
                dto.getPaymentType(),
                dto.getAmount(),
                payment.getReceiptNo(),
                dto.getFeeItem(),
                "收费入账"
        );

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean voidPayment(Long id, String reason) {
        Payment payment = paymentMapper.selectById(id);
        if (payment == null) {
            throw new BusinessException("收费记录不存在");
        }
        if ("VOIDED".equals(payment.getStatus())) {
            throw new BusinessException("该收费记录已作废");
        }

        BigDecimal currentBalance = balanceService.getBalance(payment.getChildId(), payment.getPaymentType());
        if (currentBalance.compareTo(payment.getAmount()) < 0) {
            throw new BusinessException(AccountTypeEnum.getNameByCode(payment.getPaymentType()) + "余额不足，无法作废。当前余额：" + currentBalance + "，需扣减：" + payment.getAmount());
        }

        payment.setStatus("VOIDED");
        payment.setRemark(payment.getRemark() != null ? payment.getRemark() + " | 作废原因：" + reason : "作废原因：" + reason);
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.updateById(payment);

        balanceService.deductBalance(
                payment.getChildId(),
                payment.getPaymentType(),
                payment.getAmount(),
                payment.getReceiptNo(),
                payment.getFeeItem(),
                "收费作废：" + reason
        );

        return true;
    }

    @Override
    public List<PaymentVO> getByChildId(Long childId) {
        LambdaQueryWrapper<Payment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Payment::getChildId, childId)
                .orderByDesc(Payment::getCreateTime);
        List<Payment> payments = paymentMapper.selectList(queryWrapper);
        return payments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private String generateReceiptNo() {
        String dateStr = LocalDate.now().toString().replace("-", "");
        long sequence = SnowflakeIdGenerator.generateId() % 10000;
        return String.format("SK%s%04d", dateStr, sequence);
    }

    private PaymentVO convertToVO(Payment payment) {
        PaymentVO vo = new PaymentVO();
        BeanUtils.copyProperties(payment, vo);
        
        Child child = childMapper.selectById(payment.getChildId());
        if (child != null) {
            vo.setChildName(child.getName());
        }
        
        return vo;
    }
}