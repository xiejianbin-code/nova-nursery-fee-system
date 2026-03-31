package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.RefundDTO;
import com.nova.backend.dto.RefundQueryDTO;
import com.nova.backend.entity.Child;
import com.nova.backend.entity.Refund;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.ChildMapper;
import com.nova.backend.mapper.RefundMapper;
import com.nova.backend.service.BalanceService;
import com.nova.backend.service.RefundService;
import com.nova.backend.util.SnowflakeIdGenerator;
import com.nova.backend.enums.AccountTypeEnum;
import com.nova.backend.vo.RefundVO;
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
 * 退费服务实现类
 *
 * @author Nova
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final RefundMapper refundMapper;
    private final ChildMapper childMapper;
    private final BalanceService balanceService;

    @Override
    public Page<RefundVO> pageList(RefundQueryDTO queryDTO) {
        Page<Refund> page = new Page<>(
                queryDTO.getPage() != null ? queryDTO.getPage() : 1,
                queryDTO.getSize() != null ? queryDTO.getSize() : 10
        );

        LambdaQueryWrapper<Refund> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(queryDTO.getRefundNo()), Refund::getRefundNo, queryDTO.getRefundNo())
                .eq(queryDTO.getChildId() != null, Refund::getChildId, queryDTO.getChildId())
                .eq(StringUtils.hasText(queryDTO.getAccountType()), Refund::getAccountType, queryDTO.getAccountType())
                .eq(StringUtils.hasText(queryDTO.getStatus()), Refund::getStatus, queryDTO.getStatus());

        // 单独处理日期条件，避免NPE
        if (queryDTO.getRefundDateBegin() != null) {
            queryWrapper.ge(Refund::getCreateTime, queryDTO.getRefundDateBegin().atStartOfDay());
        }
        if (queryDTO.getRefundDateEnd() != null) {
            queryWrapper.le(Refund::getCreateTime, queryDTO.getRefundDateEnd().atTime(23, 59, 59));
        }
        queryWrapper.orderByDesc(Refund::getCreateTime);

        Page<Refund> result = refundMapper.selectPage(page, queryWrapper);

        Page<RefundVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(RefundDTO dto) {
        Child child = childMapper.selectById(dto.getChildId());
        if (child == null) {
            throw new BusinessException("幼儿不存在");
        }

        BigDecimal currentBalance = balanceService.getBalance(dto.getChildId(), dto.getAccountType());
        if (currentBalance.compareTo(dto.getAmount()) < 0) {
            throw new BusinessException(AccountTypeEnum.getNameByCode(dto.getAccountType()) + "余额不足，无法退费。当前余额：" + currentBalance + "，申请退费：" + dto.getAmount());
        }

        Refund refund = new Refund();
        BeanUtils.copyProperties(dto, refund);
        refund.setRefundNo(generateRefundNo());
        refund.setStatus("COMPLETED");
        refund.setCreateTime(LocalDateTime.now());
        refund.setUpdateTime(LocalDateTime.now());
        refundMapper.insert(refund);

        balanceService.deductBalance(
                dto.getChildId(),
                dto.getAccountType(),
                dto.getAmount(),
                refund.getRefundNo(),
                dto.getFeeItem(),
                "退费：" + dto.getReason()
        );

        return true;
    }

    @Override
    public List<RefundVO> getByChildId(Long childId) {
        LambdaQueryWrapper<Refund> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Refund::getChildId, childId)
                .orderByDesc(Refund::getCreateTime);
        List<Refund> refunds = refundMapper.selectList(queryWrapper);
        return refunds.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private String generateRefundNo() {
        String dateStr = LocalDate.now().toString().replace("-", "");
        long sequence = SnowflakeIdGenerator.generateId() % 10000;
        return String.format("TF%s%04d", dateStr, sequence);
    }

    private RefundVO convertToVO(Refund refund) {
        RefundVO vo = new RefundVO();
        BeanUtils.copyProperties(refund, vo);
        
        Child child = childMapper.selectById(refund.getChildId());
        if (child != null) {
            vo.setChildName(child.getName());
        }
        
        return vo;
    }
}