package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.BalanceRecordQueryDTO;
import com.nova.backend.entity.BalanceAccount;
import com.nova.backend.entity.BalanceRecord;
import com.nova.backend.entity.Child;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.BalanceAccountMapper;
import com.nova.backend.mapper.BalanceRecordMapper;
import com.nova.backend.mapper.ChildMapper;
import com.nova.backend.service.BalanceService;
import com.nova.backend.enums.AccountTypeEnum;
import com.nova.backend.vo.BalanceRecordVO;
import com.nova.backend.vo.BalanceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 余额服务实现类
 *
 * @author Nova
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceAccountMapper balanceAccountMapper;
    private final BalanceRecordMapper balanceRecordMapper;
    private final ChildMapper childMapper;

    private static final String[] ACCOUNT_TYPES = {"EDUCATION", "MEAL", "OTHER"};

    @Override
    public BigDecimal getBalance(Long childId, String accountType) {
        LambdaQueryWrapper<BalanceAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BalanceAccount::getChildId, childId)
                .eq(BalanceAccount::getAccountType, accountType);
        BalanceAccount account = balanceAccountMapper.selectOne(queryWrapper);
        
        if (account == null) {
            return BigDecimal.ZERO;
        }
        return account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
    }

    @Override
    public BalanceVO getAllBalances(Long childId) {
        Child child = childMapper.selectById(childId);
        if (child == null) {
            throw new BusinessException("幼儿不存在");
        }

        BalanceVO vo = new BalanceVO();
        vo.setChildId(childId);
        vo.setChildName(child.getName());

        List<BalanceVO.AccountBalance> accounts = new ArrayList<>();
        for (String accountType : ACCOUNT_TYPES) {
            BalanceVO.AccountBalance accountBalance = new BalanceVO.AccountBalance();
            accountBalance.setAccountType(accountType);
            accountBalance.setAccountTypeName(getAccountTypeName(accountType));
            accountBalance.setBalance(getBalance(childId, accountType));
            accounts.add(accountBalance);
        }
        vo.setAccounts(accounts);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addBalance(Long childId, String accountType, BigDecimal amount, 
                              String relatedNo, String feeItem, String remark) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("金额必须大于0");
        }

        BalanceAccount account = getOrCreateAccount(childId, accountType);
        BigDecimal beforeBalance = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
        BigDecimal afterBalance = beforeBalance.add(amount);
        
        account.setBalance(afterBalance);
        account.setUpdateTime(LocalDateTime.now());
        balanceAccountMapper.updateById(account);

        createBalanceRecord(childId, accountType, "INCOME", amount, afterBalance, relatedNo, feeItem, remark);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductBalance(Long childId, String accountType, BigDecimal amount,
                                  String relatedNo, String feeItem, String remark) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("金额必须大于0");
        }

        BalanceAccount account = getOrCreateAccount(childId, accountType);
        BigDecimal beforeBalance = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
        
        if (beforeBalance.compareTo(amount) < 0) {
            throw new BusinessException(AccountTypeEnum.getNameByCode(accountType) + "余额不足。当前余额：" + beforeBalance + "，需扣减：" + amount);
        }
        
        BigDecimal afterBalance = beforeBalance.subtract(amount);
        account.setBalance(afterBalance);
        account.setUpdateTime(LocalDateTime.now());
        balanceAccountMapper.updateById(account);

        createBalanceRecord(childId, accountType, "EXPENSE", amount, afterBalance, relatedNo, feeItem, remark);

        return true;
    }

    @Override
    public Page<BalanceRecordVO> getBalanceRecords(BalanceRecordQueryDTO queryDTO) {
        Page<BalanceRecord> page = new Page<>(
                queryDTO.getPage() != null ? queryDTO.getPage() : 1,
                queryDTO.getSize() != null ? queryDTO.getSize() : 10
        );

        LambdaQueryWrapper<BalanceRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getChildId() != null, BalanceRecord::getChildId, queryDTO.getChildId())
                .eq(StringUtils.hasText(queryDTO.getAccountType()), BalanceRecord::getAccountType, queryDTO.getAccountType())
                .eq(StringUtils.hasText(queryDTO.getChangeType()), BalanceRecord::getChangeType, queryDTO.getChangeType())
                .orderByDesc(BalanceRecord::getCreateTime);

        Page<BalanceRecord> result = balanceRecordMapper.selectPage(page, queryWrapper);

        Page<BalanceRecordVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return voPage;
    }

    private BalanceAccount getOrCreateAccount(Long childId, String accountType) {
        LambdaQueryWrapper<BalanceAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BalanceAccount::getChildId, childId)
                .eq(BalanceAccount::getAccountType, accountType);
        BalanceAccount account = balanceAccountMapper.selectOne(queryWrapper);
        
        if (account == null) {
            account = new BalanceAccount();
            account.setChildId(childId);
            account.setAccountType(accountType);
            account.setBalance(BigDecimal.ZERO);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            balanceAccountMapper.insert(account);
        }
        
        return account;
    }

    private void createBalanceRecord(Long childId, String accountType, String changeType,
                                     BigDecimal amount, BigDecimal afterBalance, 
                                     String relatedNo, String feeItem, String remark) {
        BalanceRecord record = new BalanceRecord();
        record.setChildId(childId);
        record.setAccountType(accountType);
        record.setChangeType(changeType);
        record.setAmount(amount);
        record.setAfterBalance(afterBalance);
        record.setRelatedNo(relatedNo);
        record.setFeeItem(feeItem);
        record.setRemark(remark);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        balanceRecordMapper.insert(record);
    }

    private BalanceRecordVO convertToVO(BalanceRecord record) {
        BalanceRecordVO vo = new BalanceRecordVO();
        BeanUtils.copyProperties(record, vo);
        
        vo.setAccountTypeName(getAccountTypeName(record.getAccountType()));
        vo.setChangeTypeName(getChangeTypeName(record.getChangeType()));
        
        Child child = childMapper.selectById(record.getChildId());
        if (child != null) {
            vo.setChildName(child.getName());
        }
        
        return vo;
    }

    private String getAccountTypeName(String accountType) {
        return AccountTypeEnum.getNameByCode(accountType);
    }

    private String getChangeTypeName(String changeType) {
        switch (changeType) {
            case "INCOME":
                return "收入";
            case "EXPENSE":
                return "支出";
            default:
                return changeType;
        }
    }
}