package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nova.backend.entity.Attendance;
import com.nova.backend.entity.Bill;
import com.nova.backend.entity.Child;
import com.nova.backend.entity.Contract;
import com.nova.backend.entity.ContractFeeItem;
import com.nova.backend.entity.ContractOtherFee;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.AttendanceMapper;
import com.nova.backend.mapper.BillMapper;
import com.nova.backend.mapper.ChildMapper;
import com.nova.backend.mapper.ContractFeeItemMapper;
import com.nova.backend.mapper.ContractOtherFeeMapper;
import com.nova.backend.service.BillCalculateService;
import com.nova.backend.service.SystemConfigService;
import com.nova.backend.util.DateUtil;
import com.nova.backend.vo.AttendanceStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillCalculateServiceImpl implements BillCalculateService {

    private final ContractFeeItemMapper contractFeeItemMapper;
    private final ContractOtherFeeMapper contractOtherFeeMapper;
    private final ChildMapper childMapper;
    private final BillMapper billMapper;
    private final AttendanceMapper attendanceMapper;
    private final SystemConfigService systemConfigService;

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final int ATTENDANCE_STATUS_LEAVE = 2;
    private static final int ATTENDANCE_STATUS_SICK = 3;

    @Override
    public BigDecimal calculateEducationFee(Contract contract, Integer year, Integer month) {
        if (contract == null) {
            return ZERO;
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        LocalDate contractStart = contract.getStartDate();
        LocalDate contractEnd = contract.getEndDate();

        if (contractStart == null) {
            return ZERO;
        }

        if (contractEnd != null && contractEnd.isBefore(monthStart)) {
            return ZERO;
        }

        if (contractStart.isAfter(monthEnd)) {
            return ZERO;
        }

        LocalDate effectiveStart = contractStart.isBefore(monthStart) ? monthStart : contractStart;
        LocalDate effectiveEnd = (contractEnd == null || contractEnd.isAfter(monthEnd)) ? monthEnd : contractEnd;

        if (effectiveStart.isAfter(effectiveEnd)) {
            return ZERO;
        }

        int totalWorkingDays = getWorkingDays(year, month);
        if (totalWorkingDays == 0) {
            return ZERO;
        }

        BigDecimal totalEducationFee = ZERO;
        LocalDate currentDate = effectiveStart;

        while (!currentDate.isAfter(effectiveEnd)) {
            if (DateUtil.isWorkDay(currentDate)) {
                int ageMonths = calculateAgeMonths(contract.getChildId(), currentDate);
                BigDecimal monthlyFee = getEducationFeeByAge(contract.getId(), ageMonths);
                BigDecimal dailyFee = monthlyFee.divide(BigDecimal.valueOf(totalWorkingDays), 4, RoundingMode.HALF_UP);
                totalEducationFee = totalEducationFee.add(dailyFee);
            }
            currentDate = currentDate.plusDays(1);
        }

        return totalEducationFee.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateMealFee(Contract contract, Integer year, Integer month) {
        if (contract == null) {
            return ZERO;
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate monthStart = yearMonth.atDay(1);

        int monthAge = calculateAgeMonths(contract.getChildId(), monthStart);
        BigDecimal monthlyMealFee = getMealFeeByAge(contract.getId(), monthAge);

        return monthlyMealFee.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateOtherFee(Contract contract, Integer year, Integer month) {
        if (contract == null) {
            return ZERO;
        }

        LambdaQueryWrapper<ContractOtherFee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractOtherFee::getContractId, contract.getId())
                .eq(ContractOtherFee::getStatus, 1);
        List<ContractOtherFee> otherFees = contractOtherFeeMapper.selectList(queryWrapper);

        if (otherFees == null || otherFees.isEmpty()) {
            return ZERO;
        }

        BigDecimal totalOtherFee = ZERO;
        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate contractStartDate = contract.getStartDate();
        boolean isContractStartMonth = false;
        if (contractStartDate != null) {
            YearMonth contractStartYearMonth = YearMonth.from(contractStartDate);
            isContractStartMonth = contractStartYearMonth.equals(yearMonth);
        }

        for (ContractOtherFee otherFee : otherFees) {
            String chargeCycle = otherFee.getChargeCycle();
            BigDecimal feeStandard = otherFee.getFeeStandard();

            if (feeStandard == null || feeStandard.compareTo(ZERO) <= 0) {
                continue;
            }

            switch (chargeCycle) {
                case "ONCE":
                    if (isFirstBillForOnceFee(contract.getChildId(), otherFee.getFeeName())) {
                        totalOtherFee = totalOtherFee.add(feeStandard);
                    }
                    break;
                case "MONTHLY":
                    totalOtherFee = totalOtherFee.add(feeStandard);
                    break;
                case "SEMESTER":
                    if (isContractStartMonth) {
                        totalOtherFee = totalOtherFee.add(feeStandard);
                    }
                    break;
                default:
                    log.warn("未知的收费周期类型: {}", chargeCycle);
            }
        }

        return totalOtherFee.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isFirstBillForOnceFee(Long childId, String feeName) {
        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Bill::getChildId, childId)
                .eq(Bill::getStatus, "CONFIRMED");
        List<Bill> confirmedBills = billMapper.selectList(queryWrapper);
        
        return confirmedBills == null || confirmedBills.isEmpty();
    }

    @Override
    public BigDecimal calculateEducationRefund(BigDecimal educationFee, AttendanceStatisticsVO attendance, Integer year, Integer month) {
        if (attendance == null) {
            return ZERO;
        }

        int totalWorkingDays = getWorkingDays(year, month);
        if (totalWorkingDays == 0) {
            return ZERO;
        }

        int leaveThreshold = systemConfigService.getConfigValueAsInt("LEAVE_REFUND_THRESHOLD", 3);
        int sickThreshold = systemConfigService.getConfigValueAsInt("SICK_REFUND_THRESHOLD", 3);

        int leaveDays = attendance.getLeaveDays() != null ? attendance.getLeaveDays() : 0;
        int sickDays = attendance.getSickDays() != null ? attendance.getSickDays() : 0;

        if (leaveDays <= leaveThreshold && sickDays <= sickThreshold) {
            return ZERO;
        }

        if (educationFee == null || educationFee.compareTo(ZERO) <= 0) {
            return ZERO;
        }

        BigDecimal refund = ZERO;
        BigDecimal dailyFee = educationFee.divide(BigDecimal.valueOf(totalWorkingDays), 4, RoundingMode.HALF_UP);
        
        if (leaveDays > leaveThreshold) {
            refund = refund.add(dailyFee.multiply(BigDecimal.valueOf(leaveDays)));
        }
        
        if (sickDays > sickThreshold) {
            refund = refund.add(dailyFee.multiply(BigDecimal.valueOf(sickDays)));
        }

        return refund.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateEducationRefundByAge(Contract contract, BigDecimal educationFee, 
            AttendanceStatisticsVO attendance, Integer year, Integer month) {
        if (contract == null || attendance == null) {
            return calculateEducationRefund(educationFee, attendance, year, month);
        }

        int totalWorkingDays = getWorkingDays(year, month);
        if (totalWorkingDays == 0) {
            return ZERO;
        }

        int leaveThreshold = systemConfigService.getConfigValueAsInt("LEAVE_REFUND_THRESHOLD", 3);
        int sickThreshold = systemConfigService.getConfigValueAsInt("SICK_REFUND_THRESHOLD", 3);

        int leaveDays = attendance.getLeaveDays() != null ? attendance.getLeaveDays() : 0;
        int sickDays = attendance.getSickDays() != null ? attendance.getSickDays() : 0;

        if (leaveDays <= leaveThreshold && sickDays <= sickThreshold) {
            return ZERO;
        }

        Long childId = contract.getChildId();

        List<LocalDate> leaveDates = getAttendanceDatesByStatus(childId, year, month, ATTENDANCE_STATUS_LEAVE);
        List<LocalDate> sickDates = getAttendanceDatesByStatus(childId, year, month, ATTENDANCE_STATUS_SICK);

        BigDecimal totalRefund = ZERO;

        if (leaveDays > leaveThreshold && !leaveDates.isEmpty()) {
            BigDecimal leaveRefund = calculateRefundByDates(contract, leaveDates, totalWorkingDays);
            totalRefund = totalRefund.add(leaveRefund);
        }

        if (sickDays > sickThreshold && !sickDates.isEmpty()) {
            BigDecimal sickRefund = calculateRefundByDates(contract, sickDates, totalWorkingDays);
            totalRefund = totalRefund.add(sickRefund);
        }

        return totalRefund.setScale(2, RoundingMode.HALF_UP);
    }

    private List<LocalDate> getAttendanceDatesByStatus(Long childId, Integer year, Integer month, Integer status) {
        LambdaQueryWrapper<Attendance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attendance::getChildId, childId)
                .eq(Attendance::getStatus, status);
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        queryWrapper.ge(Attendance::getAttendanceDate, startDate)
                .le(Attendance::getAttendanceDate, endDate);
        
        List<Attendance> attendances = attendanceMapper.selectList(queryWrapper);
        
        List<LocalDate> dates = new ArrayList<>();
        if (attendances != null) {
            for (Attendance attendance : attendances) {
                if (attendance.getAttendanceDate() != null) {
                    dates.add(attendance.getAttendanceDate());
                }
            }
        }
        
        return dates;
    }

    private BigDecimal calculateRefundByDates(Contract contract, List<LocalDate> dates, int totalWorkingDays) {
        if (dates == null || dates.isEmpty()) {
            return ZERO;
        }

        Map<Integer, List<LocalDate>> ageGroupDates = new HashMap<>();
        
        for (LocalDate date : dates) {
            if (!DateUtil.isWorkDay(date)) {
                continue;
            }
            int ageMonths = calculateAgeMonths(contract.getChildId(), date);
            ageGroupDates.computeIfAbsent(ageMonths, k -> new ArrayList<>()).add(date);
        }

        BigDecimal totalRefund = ZERO;

        for (Map.Entry<Integer, List<LocalDate>> entry : ageGroupDates.entrySet()) {
            int ageMonths = entry.getKey();
            int days = entry.getValue().size();
            
            BigDecimal monthlyFee = getEducationFeeByAge(contract.getId(), ageMonths);
            BigDecimal dailyFee = monthlyFee.divide(BigDecimal.valueOf(totalWorkingDays), 4, RoundingMode.HALF_UP);
            
            totalRefund = totalRefund.add(dailyFee.multiply(BigDecimal.valueOf(days)));
        }

        return totalRefund;
    }

    @Override
    public BigDecimal calculateMealRefund(BigDecimal mealFee, AttendanceStatisticsVO attendance, Integer year, Integer month) {
        if (mealFee == null || mealFee.compareTo(ZERO) <= 0) {
            return ZERO;
        }

        if (attendance == null) {
            return ZERO;
        }

        int totalWorkingDays = getWorkingDays(year, month);
        if (totalWorkingDays == 0) {
            return ZERO;
        }

        int normalDays = attendance.getNormalDays() != null ? attendance.getNormalDays() : 0;
        int totalDays = attendance.getTotalDays() != null ? attendance.getTotalDays() : totalWorkingDays;

        int absentDays = totalDays - normalDays;
        if (absentDays < 0) {
            absentDays = 0;
        }

        BigDecimal dailyMealFee = mealFee.divide(BigDecimal.valueOf(totalWorkingDays), 4, RoundingMode.HALF_UP);
        BigDecimal refund = dailyMealFee.multiply(BigDecimal.valueOf(absentDays));

        return refund.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int getWorkingDays(Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        int workingDays = 0;
        LocalDate current = start;
        while (!current.isAfter(end)) {
            if (DateUtil.isWorkDay(current)) {
                workingDays++;
            }
            current = current.plusDays(1);
        }

        return workingDays;
    }

    @Override
    public BigDecimal getEducationFeeByAge(Long contractId, Integer ageMonths) {
        LambdaQueryWrapper<ContractFeeItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractFeeItem::getContractId, contractId);
        List<ContractFeeItem> feeItems = contractFeeItemMapper.selectList(queryWrapper);

        if (feeItems == null || feeItems.isEmpty()) {
            throw new BusinessException("合同未配置年龄段收费明细");
        }

        for (ContractFeeItem item : feeItems) {
            int[] range = parseAgeRange(item.getAgeRange());
            if (range != null && ageMonths >= range[0] && ageMonths <= range[1]) {
                return item.getEducationFee() != null ? item.getEducationFee() : ZERO;
            }
        }

        for (ContractFeeItem item : feeItems) {
            if (item.getAgeRange().contains("以上")) {
                int[] range = parseAgeRange(item.getAgeRange());
                if (range != null && ageMonths >= range[0]) {
                    return item.getEducationFee() != null ? item.getEducationFee() : ZERO;
                }
            }
        }

        throw new BusinessException("未找到对应年龄段的收费标准");
    }

    @Override
    public BigDecimal getMealFeeByAge(Long contractId, Integer ageMonths) {
        LambdaQueryWrapper<ContractFeeItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractFeeItem::getContractId, contractId);
        List<ContractFeeItem> feeItems = contractFeeItemMapper.selectList(queryWrapper);

        if (feeItems == null || feeItems.isEmpty()) {
            throw new BusinessException("合同未配置年龄段收费明细");
        }

        for (ContractFeeItem item : feeItems) {
            int[] range = parseAgeRange(item.getAgeRange());
            if (range != null && ageMonths >= range[0] && ageMonths <= range[1]) {
                return item.getMealFee() != null ? item.getMealFee() : ZERO;
            }
        }

        for (ContractFeeItem item : feeItems) {
            if (item.getAgeRange().contains("以上")) {
                int[] range = parseAgeRange(item.getAgeRange());
                if (range != null && ageMonths >= range[0]) {
                    return item.getMealFee() != null ? item.getMealFee() : ZERO;
                }
            }
        }

        throw new BusinessException("未找到对应年龄段的收费标准");
    }

    private int calculateAgeMonths(Long childId, LocalDate date) {
        Child child = childMapper.selectById(childId);
        if (child == null || child.getBirthDate() == null) {
            return 0;
        }

        long months = ChronoUnit.MONTHS.between(child.getBirthDate(), date);
        return (int) months;
    }

    private int[] parseAgeRange(String ageRange) {
        if (ageRange == null || ageRange.isEmpty()) {
            return null;
        }

        if (ageRange.contains("以上")) {
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(ageRange);
            if (matcher.find()) {
                int min = Integer.parseInt(matcher.group(1));
                return new int[]{min, Integer.MAX_VALUE};
            }
        }

        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(ageRange);
        if (matcher.find()) {
            int min = Integer.parseInt(matcher.group(1));
            int max = Integer.parseInt(matcher.group(2));
            return new int[]{min, max};
        }

        return null;
    }
}
