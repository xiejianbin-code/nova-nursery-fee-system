package com.nova.backend.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nova.backend.common.ContractStatus;
import com.nova.backend.entity.*;
import com.nova.backend.enums.AlertTypeEnum;
import com.nova.backend.mapper.*;
import com.nova.backend.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 预警定时任务
 *
 * @author Nova
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class AlertTask {

    private final AlertService alertService;
    private final ContractMapper contractMapper;
    private final ChildMapper childMapper;
    private final BillMapper billMapper;
    private final BalanceAccountMapper balanceAccountMapper;
    private final AlertMapper alertMapper;

    

    private static final String CHILD_STATUS_IN_GARDEN = "IN_GARDEN";
    private static final String BILL_STATUS_CONFIRMED = "CONFIRMED";
    private static final String HANDLE_STATUS_PENDING = "PENDING";

    private static final int[] CONTRACT_EXPIRE_DAYS = {60, 30, 15, 7};

    private static final int OVERDUE_GRACE_DAYS = 7;

    @Scheduled(cron = "0 0 8 * * ?")
    public void checkContractExpire() {
        log.info("开始执行合同到期预警检查...");

        LocalDate today = LocalDate.now();

        for (int days : CONTRACT_EXPIRE_DAYS) {
            LocalDate targetDate = today.plusDays(days);

            LambdaQueryWrapper<Contract> contractWrapper = new LambdaQueryWrapper<>();
            contractWrapper.eq(Contract::getStatus, ContractStatus.ACTIVE)
                    .eq(Contract::getEndDate, targetDate);
            List<Contract> contracts = contractMapper.selectList(contractWrapper);

            for (Contract contract : contracts) {
                Child child = childMapper.selectById(contract.getChildId());
                if (child == null || !CHILD_STATUS_IN_GARDEN.equals(child.getStatus())) {
                    continue;
                }

                if (hasPendingAlert(contract.getChildId(), AlertTypeEnum.CONTRACT_EXPIRE.getCode(), days)) {
                    continue;
                }

                String content = String.format("幼儿【%s】的合同将于%d天后到期（%s），请及时处理续签或变更",
                        child.getName(), days, targetDate);
                alertService.createAlert(contract.getChildId(), AlertTypeEnum.CONTRACT_EXPIRE.getCode(), content);
                log.info("创建合同到期预警：幼儿ID={}，剩余天数={}", contract.getChildId(), days);
            }
        }

        log.info("合同到期预警检查完成");
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void checkFeeExpire() {
        log.info("开始执行保教费余额不足预警检查...");

        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        String nextMonthStr = nextMonth.toString();

        LambdaQueryWrapper<Child> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Child::getStatus, CHILD_STATUS_IN_GARDEN);
        List<Child> children = childMapper.selectList(childWrapper);

        for (Child child : children) {
            LambdaQueryWrapper<BalanceAccount> balanceWrapper = new LambdaQueryWrapper<>();
            balanceWrapper.eq(BalanceAccount::getChildId, child.getId())
                    .eq(BalanceAccount::getAccountType, "EDUCATION");
            BalanceAccount educationAccount = balanceAccountMapper.selectOne(balanceWrapper);

            if (educationAccount == null || educationAccount.getBalance() == null) {
                continue;
            }

            BigDecimal balance = educationAccount.getBalance();
            if (balance.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            LambdaQueryWrapper<Bill> billWrapper = new LambdaQueryWrapper<>();
            billWrapper.eq(Bill::getChildId, child.getId())
                    .eq(Bill::getBillMonth, nextMonthStr);
            Bill nextBill = billMapper.selectOne(billWrapper);

            if (nextBill == null || nextBill.getDueAmount() == null) {
                continue;
            }

            if (balance.compareTo(nextBill.getDueAmount()) < 0) {
                if (hasPendingAlert(child.getId(), AlertTypeEnum.EDUCATION_FEE_LOW.getCode(), null)) {
                    continue;
                }

                String content = String.format("幼儿【%s】的保教费余额（%.2f元）不足以覆盖下月账单（%.2f元），请及时充值",
                        child.getName(), balance, nextBill.getDueAmount());
                alertService.createAlert(child.getId(), AlertTypeEnum.EDUCATION_FEE_LOW.getCode(), content);
                log.info("创建保教费余额不足预警：幼儿ID={}", child.getId());
            }
        }

        log.info("保教费余额不足预警检查完成");
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void checkOverdue() {
        log.info("开始执行逾期未缴费预警检查...");

        LocalDate today = LocalDate.now();
        LocalDate gracePeriodStart = today.minusDays(OVERDUE_GRACE_DAYS);

        LambdaQueryWrapper<Bill> billWrapper = new LambdaQueryWrapper<>();
        billWrapper.eq(Bill::getStatus, BILL_STATUS_CONFIRMED)
                .le(Bill::getConfirmTime, gracePeriodStart.atStartOfDay())
                .isNotNull(Bill::getDueAmount)
                .gt(Bill::getDueAmount, BigDecimal.ZERO);
        List<Bill> bills = billMapper.selectList(billWrapper);

        for (Bill bill : bills) {
            Child child = childMapper.selectById(bill.getChildId());
            if (child == null || !CHILD_STATUS_IN_GARDEN.equals(child.getStatus())) {
                continue;
            }

            if (hasPendingAlert(child.getId(), AlertTypeEnum.OVERDUE.getCode(), null)) {
                continue;
            }

            long overdueDays = ChronoUnit.DAYS.between(bill.getConfirmTime().toLocalDate(), today) - OVERDUE_GRACE_DAYS;

            String content = String.format("幼儿【%s】的账单（%s）已逾期%d天未缴费，应收金额%.2f元，请及时催缴",
                    child.getName(), bill.getBillMonth(), overdueDays, bill.getDueAmount());
            alertService.createAlert(child.getId(), AlertTypeEnum.OVERDUE.getCode(), content);
            log.info("创建逾期未缴费预警：幼儿ID={}，账单月份={}", child.getId(), bill.getBillMonth());
        }

        log.info("逾期未缴费预警检查完成");
    }

    private boolean hasPendingAlert(Long childId, String alertType, Integer days) {
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Alert::getChildId, childId)
                .eq(Alert::getAlertType, alertType)
                .eq(Alert::getHandleStatus, HANDLE_STATUS_PENDING);

        if (days != null) {
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = todayStart.plusDays(1);
            wrapper.ge(Alert::getCreateTime, todayStart)
                    .lt(Alert::getCreateTime, todayEnd);
        }

        return alertMapper.selectCount(wrapper) > 0;
    }
}