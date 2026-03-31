package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nova.backend.common.ContractStatus;
import com.nova.backend.entity.*;
import com.nova.backend.mapper.*;
import com.nova.backend.service.ReportService;
import com.nova.backend.vo.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表服务实现类
 *
 * @author Nova
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final BillMapper billMapper;
    private final BillItemMapper billItemMapper;
    private final ChildMapper childMapper;
    private final ClazzMapper clazzMapper;
    private final ContractMapper contractMapper;
    private final BalanceAccountMapper balanceAccountMapper;
    private final PaymentMapper paymentMapper;
    private final ContractOtherFeeMapper contractOtherFeeMapper;

    private static final String CHILD_STATUS_IN_GARDEN = "IN_GARDEN";
    private static final String BILL_STATUS_CONFIRMED = "CONFIRMED";
    private static final String PAYMENT_STATUS_VALID = "VALID";
    private static final int OVERDUE_GRACE_DAYS = 7;

    @Override
    public MonthlyIncomeReportVO getMonthlyIncomeReport(Integer year, Integer month) {
        String billMonth = String.format("%d-%02d", year, month);

        LambdaQueryWrapper<Bill> billWrapper = new LambdaQueryWrapper<>();
        billWrapper.eq(Bill::getBillMonth, billMonth);
        List<Bill> bills = billMapper.selectList(billWrapper);

        MonthlyIncomeReportVO report = new MonthlyIncomeReportVO();

        BigDecimal educationFeeTotal = BigDecimal.ZERO;
        BigDecimal mealFeeTotal = BigDecimal.ZERO;
        BigDecimal otherFeeTotal = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal refundAmount = BigDecimal.ZERO;
        Set<Long> paidChildIds = new HashSet<>();

        for (Bill bill : bills) {
            educationFeeTotal = educationFeeTotal.add(bill.getEducationFeeReceivable() != null ? bill.getEducationFeeReceivable() : BigDecimal.ZERO);
            mealFeeTotal = mealFeeTotal.add(bill.getMealFeeReceivable() != null ? bill.getMealFeeReceivable() : BigDecimal.ZERO);
            otherFeeTotal = otherFeeTotal.add(bill.getOtherFeeReceivable() != null ? bill.getOtherFeeReceivable() : BigDecimal.ZERO);
            discountAmount = discountAmount.add(bill.getDiscountAmount() != null ? bill.getDiscountAmount() : BigDecimal.ZERO);

            BigDecimal educationRefund = bill.getEducationFeeRefund() != null ? bill.getEducationFeeRefund() : BigDecimal.ZERO;
            BigDecimal mealRefund = bill.getMealFeeRefund() != null ? bill.getMealFeeRefund() : BigDecimal.ZERO;
            refundAmount = refundAmount.add(educationRefund).add(mealRefund);

            BigDecimal dueAmount = bill.getDueAmount() != null ? bill.getDueAmount() : BigDecimal.ZERO;
            if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
                paidChildIds.add(bill.getChildId());
            }
        }

        LambdaQueryWrapper<Payment> paymentWrapper = new LambdaQueryWrapper<>();
        paymentWrapper.eq(Payment::getStatus, PAYMENT_STATUS_VALID);
        List<Payment> payments = paymentMapper.selectList(paymentWrapper);

        for (Payment payment : payments) {
            LocalDateTime paymentTime = payment.getCreateTime();
            if (paymentTime != null && paymentTime.getYear() == year && paymentTime.getMonthValue() == month) {
                paidAmount = paidAmount.add(payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO);
            }
        }

        BigDecimal totalReceivable = educationFeeTotal.add(mealFeeTotal).add(otherFeeTotal);
        BigDecimal unpaidAmount = totalReceivable.subtract(discountAmount).subtract(refundAmount).subtract(paidAmount);
        if (unpaidAmount.compareTo(BigDecimal.ZERO) < 0) {
            unpaidAmount = BigDecimal.ZERO;
        }

        LambdaQueryWrapper<Child> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Child::getStatus, CHILD_STATUS_IN_GARDEN);
        Long totalChildren = childMapper.selectCount(childWrapper);

        report.setEducationFeeTotal(educationFeeTotal);
        report.setMealFeeTotal(mealFeeTotal);
        report.setOtherFeeTotal(otherFeeTotal);
        report.setPaidAmount(paidAmount);
        report.setUnpaidAmount(unpaidAmount);
        report.setDiscountAmount(discountAmount);
        report.setRefundAmount(refundAmount);
        report.setTotalIncome(paidAmount);
        report.setTotalChildren(totalChildren.intValue());
        report.setPaidChildren(paidChildIds.size());

        return report;
    }

    @Override
    public List<ClassFeeReportVO> getClassFeeReport(Long classId, Integer year, Integer month) {
        String billMonth = String.format("%d-%02d", year, month);

        List<Clazz> clazzList;
        if (classId != null) {
            Clazz clazz = clazzMapper.selectById(classId);
            clazzList = clazz != null ? Collections.singletonList(clazz) : Collections.emptyList();
        } else {
            clazzList = clazzMapper.selectList(null);
        }

        List<ClassFeeReportVO> reports = new ArrayList<>();

        for (Clazz clazz : clazzList) {
            ClassFeeReportVO report = new ClassFeeReportVO();
            report.setClassId(clazz.getId());
            report.setClassName(clazz.getClassName());

            LambdaQueryWrapper<Child> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(Child::getClassId, clazz.getId())
                    .eq(Child::getStatus, CHILD_STATUS_IN_GARDEN);
            List<Child> children = childMapper.selectList(childWrapper);
            report.setStudentCount(children.size());

            if (children.isEmpty()) {
                report.setPaidCount(0);
                report.setUnpaidCount(0);
                report.setTotalAmount(BigDecimal.ZERO);
                report.setPaidAmount(BigDecimal.ZERO);
                report.setUnpaidAmount(BigDecimal.ZERO);
                report.setAvgAmount(BigDecimal.ZERO);
                report.setPaidRate(BigDecimal.ZERO);
                reports.add(report);
                continue;
            }

            List<Long> childIds = children.stream().map(Child::getId).collect(Collectors.toList());

            LambdaQueryWrapper<Bill> billWrapper = new LambdaQueryWrapper<>();
            billWrapper.eq(Bill::getBillMonth, billMonth)
                    .in(Bill::getChildId, childIds);
            List<Bill> bills = billMapper.selectList(billWrapper);

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal paidAmount = BigDecimal.ZERO;
            int paidCount = 0;

            for (Bill bill : bills) {
                BigDecimal actualAmount = bill.getActualAmount() != null ? bill.getActualAmount() : BigDecimal.ZERO;
                totalAmount = totalAmount.add(actualAmount);

                BigDecimal dueAmount = bill.getDueAmount() != null ? bill.getDueAmount() : BigDecimal.ZERO;
                if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    paidCount++;
                } else {
                    paidAmount = paidAmount.add(dueAmount);
                }
            }

            report.setTotalAmount(totalAmount);
            report.setPaidAmount(totalAmount.subtract(paidAmount));
            report.setUnpaidAmount(paidAmount);
            report.setPaidCount(paidCount);
            report.setUnpaidCount(children.size() - paidCount);

            if (children.size() > 0) {
                report.setAvgAmount(totalAmount.divide(BigDecimal.valueOf(children.size()), 2, RoundingMode.HALF_UP));
            } else {
                report.setAvgAmount(BigDecimal.ZERO);
            }

            if (children.size() > 0) {
                report.setPaidRate(BigDecimal.valueOf(paidCount * 100.0 / children.size()).setScale(2, RoundingMode.HALF_UP));
            } else {
                report.setPaidRate(BigDecimal.ZERO);
            }

            reports.add(report);
        }

        return reports;
    }

    @Override
    public List<ArrearsVO> getArrearsList() {
        LocalDate today = LocalDate.now();
        LocalDate gracePeriodStart = today.minusDays(OVERDUE_GRACE_DAYS);

        LambdaQueryWrapper<Bill> billWrapper = new LambdaQueryWrapper<>();
        billWrapper.eq(Bill::getStatus, BILL_STATUS_CONFIRMED)
                .le(Bill::getConfirmTime, gracePeriodStart.atStartOfDay())
                .isNotNull(Bill::getDueAmount)
                .gt(Bill::getDueAmount, BigDecimal.ZERO);
        List<Bill> bills = billMapper.selectList(billWrapper);

        Map<Long, List<Bill>> childBillMap = bills.stream()
                .collect(Collectors.groupingBy(Bill::getChildId));

        List<ArrearsVO> arrearsList = new ArrayList<>();

        for (Map.Entry<Long, List<Bill>> entry : childBillMap.entrySet()) {
            Long childId = entry.getKey();
            List<Bill> childBills = entry.getValue();

            Child child = childMapper.selectById(childId);
            if (child == null || !CHILD_STATUS_IN_GARDEN.equals(child.getStatus())) {
                continue;
            }

            ArrearsVO arrears = new ArrearsVO();
            arrears.setChildId(childId);
            arrears.setChildName(child.getName());
            arrears.setParentPhone(child.getContactPhone());

            Clazz clazz = child.getClassId() != null ? clazzMapper.selectById(child.getClassId()) : null;
            arrears.setClassName(clazz != null ? clazz.getClassName() : "");

            BigDecimal totalArrears = childBills.stream()
                    .map(bill -> bill.getDueAmount() != null ? bill.getDueAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            arrears.setArrearsAmount(totalArrears);

            LocalDateTime earliestConfirmTime = childBills.stream()
                    .map(Bill::getConfirmTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            if (earliestConfirmTime != null) {
                long days = ChronoUnit.DAYS.between(earliestConfirmTime.toLocalDate(), today) - OVERDUE_GRACE_DAYS;
                arrears.setOverdueDays((int) Math.max(days, 0));
            } else {
                arrears.setOverdueDays(0);
            }

            String billMonths = childBills.stream()
                    .map(Bill::getBillMonth)
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));
            arrears.setBillMonths(billMonths);

            arrearsList.add(arrears);
        }

        arrearsList.sort((a, b) -> b.getOverdueDays().compareTo(a.getOverdueDays()));

        return arrearsList;
    }

    @Override
    public List<BalanceReportVO> getBalanceReport(Long classId) {
        LambdaQueryWrapper<Child> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Child::getStatus, CHILD_STATUS_IN_GARDEN);
        if (classId != null) {
            childWrapper.eq(Child::getClassId, classId);
        }
        List<Child> children = childMapper.selectList(childWrapper);

        List<BalanceReportVO> reports = new ArrayList<>();

        for (Child child : children) {
            BalanceReportVO report = new BalanceReportVO();
            report.setChildId(child.getId());
            report.setChildName(child.getName());

            Clazz clazz = child.getClassId() != null ? clazzMapper.selectById(child.getClassId()) : null;
            report.setClassName(clazz != null ? clazz.getClassName() : "");

            LambdaQueryWrapper<BalanceAccount> balanceWrapper = new LambdaQueryWrapper<>();
            balanceWrapper.eq(BalanceAccount::getChildId, child.getId());
            List<BalanceAccount> accounts = balanceAccountMapper.selectList(balanceWrapper);

            BigDecimal educationBalance = BigDecimal.ZERO;
            BigDecimal mealBalance = BigDecimal.ZERO;
            BigDecimal otherBalance = BigDecimal.ZERO;

            for (BalanceAccount account : accounts) {
                BigDecimal balance = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
                switch (account.getAccountType()) {
                    case "EDUCATION":
                        educationBalance = balance;
                        break;
                    case "MEAL":
                        mealBalance = balance;
                        break;
                    case "OTHER":
                        otherBalance = balance;
                        break;
                }
            }

            report.setEducationBalance(educationBalance);
            report.setMealBalance(mealBalance);
            report.setOtherBalance(otherBalance);
            report.setTotalBalance(educationBalance.add(mealBalance).add(otherBalance));

            reports.add(report);
        }

        reports.sort((a, b) -> b.getTotalBalance().compareTo(a.getTotalBalance()));

        return reports;
    }

    @Override
    public ContractReportVO getContractReport() {
        ContractReportVO report = new ContractReportVO();

        Long totalContracts = contractMapper.selectCount(null);
        report.setTotalContracts(totalContracts.intValue());

        LambdaQueryWrapper<Contract> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(Contract::getStatus, ContractStatus.ACTIVE);
        Long activeContracts = contractMapper.selectCount(activeWrapper);
        report.setActiveContracts(activeContracts.intValue());

        LambdaQueryWrapper<Contract> expiredWrapper = new LambdaQueryWrapper<>();
        expiredWrapper.eq(Contract::getStatus, ContractStatus.EXPIRED);
        Long expiredContracts = contractMapper.selectCount(expiredWrapper);
        report.setExpiredContracts(expiredContracts.intValue());

        LambdaQueryWrapper<Contract> changedWrapper = new LambdaQueryWrapper<>();
        changedWrapper.eq(Contract::getStatus, ContractStatus.CHANGED);
        Long changedContracts = contractMapper.selectCount(changedWrapper);
        report.setChangedContracts(changedContracts.intValue());

        LambdaQueryWrapper<Contract> terminatedWrapper = new LambdaQueryWrapper<>();
        terminatedWrapper.eq(Contract::getStatus, ContractStatus.TERMINATED);
        Long terminatedContracts = contractMapper.selectCount(terminatedWrapper);
        report.setTerminatedContracts(terminatedContracts.intValue());

        LambdaQueryWrapper<Contract> monthlyWrapper = new LambdaQueryWrapper<>();
        monthlyWrapper.eq(Contract::getCourseType, "MONTHLY");
        Long monthlyCount = contractMapper.selectCount(monthlyWrapper);
        report.setMonthlyCount(monthlyCount.intValue());

        LambdaQueryWrapper<Contract> semesterWrapper = new LambdaQueryWrapper<>();
        semesterWrapper.eq(Contract::getCourseType, "SEMESTER");
        Long semesterCount = contractMapper.selectCount(semesterWrapper);
        report.setSemesterCount(semesterCount.intValue());

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

        LambdaQueryWrapper<Contract> newContractWrapper = new LambdaQueryWrapper<>();
        newContractWrapper.ge(Contract::getCreateTime, monthStart.atStartOfDay())
                .le(Contract::getCreateTime, monthEnd.atTime(23, 59, 59));
        Long newContractThisMonth = contractMapper.selectCount(newContractWrapper);
        report.setNewContractThisMonth(newContractThisMonth.intValue());

        LambdaQueryWrapper<Contract> expireWrapper = new LambdaQueryWrapper<>();
        expireWrapper.ge(Contract::getEndDate, monthStart)
                .le(Contract::getEndDate, monthEnd);
        Long expireContractThisMonth = contractMapper.selectCount(expireWrapper);
        report.setExpireContractThisMonth(expireContractThisMonth.intValue());

        if (totalContracts > 0) {
            report.setRenewedRate((expiredContracts.doubleValue() + changedContracts.doubleValue()) / totalContracts.doubleValue() * 100);
        } else {
            report.setRenewedRate(0.0);
        }

        return report;
    }

    @Override
    public List<OtherFeeReportVO> getOtherFeeReport(Integer year, Integer month) {
        String billMonth = String.format("%d-%02d", year, month);

        LambdaQueryWrapper<ContractOtherFee> feeWrapper = new LambdaQueryWrapper<>();
        feeWrapper.eq(ContractOtherFee::getStatus, 1);
        List<ContractOtherFee> otherFees = contractOtherFeeMapper.selectList(feeWrapper);

        Map<String, OtherFeeReportVO> feeMap = new LinkedHashMap<>();

        for (ContractOtherFee otherFee : otherFees) {
            String key = otherFee.getFeeCode() + "_" + otherFee.getChargeCycle();

            OtherFeeReportVO report = feeMap.computeIfAbsent(key, k -> {
                OtherFeeReportVO vo = new OtherFeeReportVO();
                vo.setFeeCode(otherFee.getFeeCode());
                vo.setFeeName(otherFee.getFeeName());
                vo.setChargeCycle(otherFee.getChargeCycle());
                vo.setChargeCycleName(getChargeCycleName(otherFee.getChargeCycle()));
                vo.setReceivableAmount(BigDecimal.ZERO);
                vo.setPaidAmount(BigDecimal.ZERO);
                vo.setUnpaidAmount(BigDecimal.ZERO);
                vo.setChildCount(0);
                vo.setPaidChildCount(0);
                return vo;
            });

            report.setReceivableAmount(report.getReceivableAmount().add(otherFee.getFeeStandard() != null ? otherFee.getFeeStandard() : BigDecimal.ZERO));
            report.setChildCount(report.getChildCount() + 1);
        }

        LambdaQueryWrapper<Bill> billWrapper = new LambdaQueryWrapper<>();
        billWrapper.eq(Bill::getBillMonth, billMonth);
        List<Bill> bills = billMapper.selectList(billWrapper);

        for (Bill bill : bills) {
            if (bill.getOtherFeeReceivable() != null && bill.getOtherFeeReceivable().compareTo(BigDecimal.ZERO) > 0) {
                for (OtherFeeReportVO report : feeMap.values()) {
                    if ("MONTHLY".equals(report.getChargeCycle())) {
                        report.setPaidAmount(report.getPaidAmount().add(bill.getOtherFeeReceivable()));

                        BigDecimal dueAmount = bill.getDueAmount() != null ? bill.getDueAmount() : BigDecimal.ZERO;
                        if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
                            report.setPaidChildCount(report.getPaidChildCount() + 1);
                        }
                    }
                }
            }
        }

        for (OtherFeeReportVO report : feeMap.values()) {
            report.setUnpaidAmount(report.getReceivableAmount().subtract(report.getPaidAmount()));
            if (report.getUnpaidAmount().compareTo(BigDecimal.ZERO) < 0) {
                report.setUnpaidAmount(BigDecimal.ZERO);
            }
        }

        return new ArrayList<>(feeMap.values());
    }

    @Override
    public void exportReport(String type, Integer year, Integer month, Long classId, HttpServletResponse response) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();

            String fileName;
            switch (type) {
                case "monthly-income":
                    fileName = "月度收入汇总报表";
                    exportMonthlyIncomeReport(sheet, year, month);
                    break;
                case "class-fee":
                    fileName = "班级收费统计报表";
                    exportClassFeeReport(sheet, classId, year, month);
                    break;
                case "arrears":
                    fileName = "欠费名单报表";
                    exportArrearsReport(sheet);
                    break;
                case "balance":
                    fileName = "幼儿余额统计报表";
                    exportBalanceReport(sheet, classId);
                    break;
                case "contract":
                    fileName = "合同统计报表";
                    exportContractReport(sheet);
                    break;
                case "other-fee":
                    fileName = "其他费用明细报表";
                    exportOtherFeeReport(sheet, year, month);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的报表类型: " + type);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8));

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            log.error("导出报表失败", e);
            throw new RuntimeException("导出报表失败: " + e.getMessage());
        }
    }

    private void exportMonthlyIncomeReport(Sheet sheet, Integer year, Integer month) {
        MonthlyIncomeReportVO report = getMonthlyIncomeReport(year, month);

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue(String.format("月度收入汇总报表（%d年%d月）", year, month));

        String[] headers = {"项目", "金额（元）"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 2;
        sheet.createRow(rowNum++).createCell(0).setCellValue("保教费应收");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getEducationFeeTotal().doubleValue());

        sheet.createRow(rowNum++).createCell(0).setCellValue("伙食费应收");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getMealFeeTotal().doubleValue());

        sheet.createRow(rowNum++).createCell(0).setCellValue("其他费用应收");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getOtherFeeTotal().doubleValue());

        sheet.createRow(rowNum++).createCell(0).setCellValue("已收金额");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getPaidAmount().doubleValue());

        sheet.createRow(rowNum++).createCell(0).setCellValue("未收金额");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getUnpaidAmount().doubleValue());

        sheet.createRow(rowNum++).createCell(0).setCellValue("减免金额");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getDiscountAmount().doubleValue());

        sheet.createRow(rowNum++).createCell(0).setCellValue("应退金额");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getRefundAmount().doubleValue());

        sheet.createRow(rowNum++).createCell(0).setCellValue("在园幼儿数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getTotalChildren());

        sheet.createRow(rowNum++).createCell(0).setCellValue("已缴费幼儿数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getPaidChildren());
    }

    private void exportClassFeeReport(Sheet sheet, Long classId, Integer year, Integer month) {
        List<ClassFeeReportVO> reports = getClassFeeReport(classId, year, month);

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue(String.format("班级收费统计报表（%d年%d月）", year, month));

        String[] headers = {"班级名称", "在册幼儿数", "已缴费幼儿数", "未缴费幼儿数", "应收总金额", "已收金额", "未收金额", "人均应收", "收费率"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 2;
        for (ClassFeeReportVO report : reports) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(report.getClassName());
            row.createCell(1).setCellValue(report.getStudentCount());
            row.createCell(2).setCellValue(report.getPaidCount());
            row.createCell(3).setCellValue(report.getUnpaidCount());
            row.createCell(4).setCellValue(report.getTotalAmount().doubleValue());
            row.createCell(5).setCellValue(report.getPaidAmount().doubleValue());
            row.createCell(6).setCellValue(report.getUnpaidAmount().doubleValue());
            row.createCell(7).setCellValue(report.getAvgAmount().doubleValue());
            row.createCell(8).setCellValue(report.getPaidRate().doubleValue() + "%");
        }
    }

    private void exportArrearsReport(Sheet sheet) {
        List<ArrearsVO> arrearsList = getArrearsList();

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("欠费名单报表");

        String[] headers = {"幼儿姓名", "班级", "家长电话", "欠费金额", "逾期天数", "欠费账单月份"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 2;
        for (ArrearsVO arrears : arrearsList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(arrears.getChildName());
            row.createCell(1).setCellValue(arrears.getClassName());
            row.createCell(2).setCellValue(arrears.getParentPhone());
            row.createCell(3).setCellValue(arrears.getArrearsAmount().doubleValue());
            row.createCell(4).setCellValue(arrears.getOverdueDays());
            row.createCell(5).setCellValue(arrears.getBillMonths());
        }
    }

    private void exportBalanceReport(Sheet sheet, Long classId) {
        List<BalanceReportVO> reports = getBalanceReport(classId);

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("幼儿余额统计报表");

        String[] headers = {"幼儿姓名", "班级", "保教费余额", "伙食费余额", "其他费用余额", "总余额"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 2;
        for (BalanceReportVO report : reports) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(report.getChildName());
            row.createCell(1).setCellValue(report.getClassName());
            row.createCell(2).setCellValue(report.getEducationBalance().doubleValue());
            row.createCell(3).setCellValue(report.getMealBalance().doubleValue());
            row.createCell(4).setCellValue(report.getOtherBalance().doubleValue());
            row.createCell(5).setCellValue(report.getTotalBalance().doubleValue());
        }
    }

    private void exportContractReport(Sheet sheet) {
        ContractReportVO report = getContractReport();

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("合同统计报表");

        String[] headers = {"项目", "数量"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 2;
        sheet.createRow(rowNum++).createCell(0).setCellValue("合同总数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getTotalContracts());

        sheet.createRow(rowNum++).createCell(0).setCellValue("生效中合同数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getActiveContracts());

        sheet.createRow(rowNum++).createCell(0).setCellValue("已过期合同数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getExpiredContracts());

        sheet.createRow(rowNum++).createCell(0).setCellValue("已变更合同数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getChangedContracts());

        sheet.createRow(rowNum++).createCell(0).setCellValue("已终止合同数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getTerminatedContracts());

        sheet.createRow(rowNum++).createCell(0).setCellValue("续签率");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(String.format("%.2f%%", report.getRenewedRate()));

        sheet.createRow(rowNum++).createCell(0).setCellValue("按月收费合同数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getMonthlyCount());

        sheet.createRow(rowNum++).createCell(0).setCellValue("按学期收费合同数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getSemesterCount());

        sheet.createRow(rowNum++).createCell(0).setCellValue("本月新增合同数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getNewContractThisMonth());

        sheet.createRow(rowNum++).createCell(0).setCellValue("本月到期合同数");
        sheet.getRow(rowNum - 1).createCell(1).setCellValue(report.getExpireContractThisMonth());
    }

    private void exportOtherFeeReport(Sheet sheet, Integer year, Integer month) {
        List<OtherFeeReportVO> reports = getOtherFeeReport(year, month);

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue(String.format("其他费用明细报表（%d年%d月）", year, month));

        String[] headers = {"费用名称", "收费周期", "应收金额", "已收金额", "未收金额", "涉及幼儿数", "已缴费幼儿数"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 2;
        for (OtherFeeReportVO report : reports) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(report.getFeeName());
            row.createCell(1).setCellValue(report.getChargeCycleName());
            row.createCell(2).setCellValue(report.getReceivableAmount().doubleValue());
            row.createCell(3).setCellValue(report.getPaidAmount().doubleValue());
            row.createCell(4).setCellValue(report.getUnpaidAmount().doubleValue());
            row.createCell(5).setCellValue(report.getChildCount());
            row.createCell(6).setCellValue(report.getPaidChildCount());
        }
    }

    private String getChargeCycleName(String chargeCycle) {
        if (chargeCycle == null) {
            return "";
        }
        switch (chargeCycle) {
            case "ONCE":
                return "一次性";
            case "MONTHLY":
                return "按月";
            case "SEMESTER":
                return "按学期";
            default:
                return chargeCycle;
        }
    }
}
