package com.nova.backend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.BillBatchGenerateDTO;
import com.nova.backend.dto.BillDiscountDTO;
import com.nova.backend.dto.BillGenerateDTO;
import com.nova.backend.dto.BillOtherFeeDTO;
import com.nova.backend.dto.BillQueryDTO;
import com.nova.backend.entity.*;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.*;
import com.nova.backend.service.BalanceService;
import com.nova.backend.service.BillCalculateService;
import com.nova.backend.service.BillService;
import com.nova.backend.service.ContractService;
import com.nova.backend.util.SnowflakeIdGenerator;
import com.nova.backend.vo.AttendanceStatisticsVO;
import com.nova.backend.vo.BillVO;
import com.nova.backend.vo.ContractVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 账单服务实现类
 *
 * @author Nova
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillMapper billMapper;
    private final BillItemMapper billItemMapper;
    private final ChildMapper childMapper;
    private final ContractMapper contractMapper;
    private final ContractFeeItemMapper contractFeeItemMapper;
    private final ContractOtherFeeMapper contractOtherFeeMapper;
    private final AttendanceMapper attendanceMapper;
    private final ClazzMapper clazzMapper;
    private final UserMapper userMapper;
    private final BillCalculateService billCalculateService;
    private final BalanceService balanceService;

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_CONFIRMED = "CONFIRMED";

    private static final Map<String, String> STATUS_DESC_MAP = new HashMap<>();
    private static final Map<String, String> ITEM_TYPE_DESC_MAP = new HashMap<>();

    static {
        STATUS_DESC_MAP.put(STATUS_PENDING, "待确认");
        STATUS_DESC_MAP.put(STATUS_CONFIRMED, "已确认");

        ITEM_TYPE_DESC_MAP.put("EDUCATION", "保教费");
        ITEM_TYPE_DESC_MAP.put("MEAL", "伙食费");
        ITEM_TYPE_DESC_MAP.put("OTHER_FEE", "其他费用");
        ITEM_TYPE_DESC_MAP.put("EDUCATION_REFUND", "保教费退费");
        ITEM_TYPE_DESC_MAP.put("MEAL_REFUND", "伙食费退费");
        ITEM_TYPE_DESC_MAP.put("DISCOUNT", "减免");
    }

    @Override
    public Page<BillVO> pageList(BillQueryDTO queryDTO) {
        Page<Bill> page = new Page<>(
                queryDTO.getPage() != null ? queryDTO.getPage() : 1,
                queryDTO.getSize() != null ? queryDTO.getSize() : 10
        );

        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(queryDTO.getBillNo()), Bill::getBillNo, queryDTO.getBillNo())
                .eq(queryDTO.getChildId() != null, Bill::getChildId, queryDTO.getChildId())
                .eq(queryDTO.getContractId() != null, Bill::getContractId, queryDTO.getContractId())
                .eq(StrUtil.isNotBlank(queryDTO.getBillMonth()), Bill::getBillMonth, queryDTO.getBillMonth())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), Bill::getStatus, queryDTO.getStatus())
                .orderByDesc(Bill::getCreateTime);

        if (queryDTO.getClassId() != null) {
            LambdaQueryWrapper<Child> childQueryWrapper = new LambdaQueryWrapper<>();
            childQueryWrapper.eq(Child::getClassId, queryDTO.getClassId());
            List<Child> children = childMapper.selectList(childQueryWrapper);
            if (CollUtil.isEmpty(children)) {
                Page<BillVO> emptyPage = new Page<>(page.getCurrent(), page.getSize(), 0);
                emptyPage.setRecords(new ArrayList<>());
                return emptyPage;
            }
            List<Long> childIds = children.stream().map(Child::getId).collect(Collectors.toList());
            queryWrapper.in(Bill::getChildId, childIds);
        }

        Page<Bill> result = billMapper.selectPage(page, queryWrapper);

        Page<BillVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public BillVO getDetailById(Long id) {
        Bill bill = billMapper.selectById(id);
        if (bill == null) {
            throw new BusinessException("账单不存在");
        }
        BillVO vo = convertToVO(bill);
        vo.setItems(getBillItems(bill.getId()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateBill(BillGenerateDTO dto, Long currentUserId) {
        Child child = childMapper.selectById(dto.getChildId());
        if (child == null) {
            throw new BusinessException("幼儿不存在");
        }

        String billMonth = String.format("%d-%02d", dto.getYear(), dto.getMonth());
        Bill existBill = billMapper.selectByChildIdAndMonth(dto.getChildId(), billMonth);
        if (existBill != null) {
            throw new BusinessException("该幼儿当月账单已存在");
        }

        ContractVO contractVO = getActiveContract(child.getId(), dto.getYear(), dto.getMonth());
        if (contractVO == null) {
            throw new BusinessException("该幼儿当月无生效合同");
        }

        Contract contract = contractMapper.selectById(contractVO.getId());

        doGenerateBill(child, contract, dto.getYear(), dto.getMonth(), currentUserId);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateMonthlyBills() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        int year = lastMonth.getYear();
        int month = lastMonth.getMonthValue();

        log.info("开始生成{}年{}月账单", year, month);

        LambdaQueryWrapper<Child> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Child::getStatus, "IN_GARDEN");
        List<Child> children = childMapper.selectList(queryWrapper);

        int count = 0;
        for (Child child : children) {
            try {
                String billMonth = String.format("%d-%02d", year, month);
                Bill existBill = billMapper.selectByChildIdAndMonth(child.getId(), billMonth);
                if (existBill != null) {
                    log.info("幼儿{}当月账单已存在，跳过", child.getName());
                    continue;
                }

                ContractVO contractVO = getActiveContract(child.getId(), year, month);
                if (contractVO == null) {
                    log.info("幼儿{}当月无生效合同，跳过", child.getName());
                    continue;
                }

                Contract contract = contractMapper.selectById(contractVO.getId());
                boolean isFirstBill = isFirstBill(child.getId(), contract.getId());

                doGenerateBill(child, contract, year, month,null);
                count++;
            } catch (Exception e) {
                log.error("生成幼儿{}账单失败: {}", child.getName(), e.getMessage(), e);
            }
        }

        log.info("账单生成完成，共生成{}条账单", count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmBill(Long id, Long currentUserId) {
        Bill bill = billMapper.selectById(id);
        if (bill == null) {
            throw new BusinessException("账单不存在");
        }

        if (!STATUS_PENDING.equals(bill.getStatus())) {
            throw new BusinessException("只有待确认状态的账单才能确认");
        }

        BigDecimal educationFeeReceivable = NumberUtil.nullToZero(bill.getEducationFeeReceivable());
        BigDecimal educationRefund = NumberUtil.nullToZero(bill.getEducationFeeRefund());
        BigDecimal educationDeduct = educationFeeReceivable.subtract(educationRefund);

        BigDecimal mealFeeReceivable = NumberUtil.nullToZero(bill.getMealFeeReceivable());
        BigDecimal mealRefund = NumberUtil.nullToZero(bill.getMealFeeRefund());
        BigDecimal mealDeduct = mealFeeReceivable.subtract(mealRefund);

        BigDecimal otherDeduct = NumberUtil.nullToZero(bill.getOtherFeeReceivable());

        if (educationDeduct.compareTo(BigDecimal.ZERO) > 0) {
            balanceService.deductBalance(bill.getChildId(), "EDUCATION", educationDeduct, bill.getBillNo(), "账单确认扣减保教费","账单确认扣减保教费");
        }
        if (mealDeduct.compareTo(BigDecimal.ZERO) > 0) {
            balanceService.deductBalance(bill.getChildId(), "MEAL", mealDeduct, bill.getBillNo(), "账单确认扣减伙食费","账单确认扣减伙食费");
        }
        if (otherDeduct.compareTo(BigDecimal.ZERO) > 0) {
            balanceService.deductBalance(bill.getChildId(), "OTHER", otherDeduct, bill.getBillNo(), "账单确认扣减其他费用","账单确认扣减其他费用");
        }
        
        bill.setStatus(STATUS_CONFIRMED);
        bill.setConfirmTime(LocalDateTime.now());
        bill.setConfirmUser(currentUserId);
        bill.setUpdateTime(LocalDateTime.now());
        bill.setUpdateUser(currentUserId);
        billMapper.updateById(bill);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recalculateBill(Long id, Long currentUserId) {
        Bill bill = billMapper.selectById(id);
        if (bill == null) {
            throw new BusinessException("账单不存在");
        }

        if (STATUS_CONFIRMED.equals(bill.getStatus())) {
            throw new BusinessException("已确认的账单不能重新核算");
        }

        String[] parts = bill.getBillMonth().split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        Child child = childMapper.selectById(bill.getChildId());
        Contract contract = contractMapper.selectById(bill.getContractId());

        LambdaQueryWrapper<BillItem> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BillItem::getBillId, id);
        billItemMapper.delete(deleteWrapper);

        doGenerateBill(child, contract, year, month, currentUserId, id);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean discountBill(BillDiscountDTO dto, Long currentUserId) {
        Bill bill = billMapper.selectById(dto.getBillId());
        if (bill == null) {
            throw new BusinessException("账单不存在");
        }

        if (STATUS_CONFIRMED.equals(bill.getStatus())) {
            throw new BusinessException("已确认的账单不能减免");
        }

        BigDecimal currentDiscount = NumberUtil.nullToZero(bill.getDiscountAmount());
        BigDecimal newDiscount = currentDiscount.add(dto.getDiscountAmount());
        bill.setDiscountAmount(newDiscount);

        BigDecimal actualAmount = calculateActualAmount(bill);
        if (actualAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("减免金额不能超过实际应交金额");
        }

        bill.setDiscountAmount(newDiscount);
        bill.setActualAmount(actualAmount);
        bill.setDueAmount(calculateDueAmount(bill));
        bill.setUpdateTime(LocalDateTime.now());
        bill.setUpdateUser(currentUserId);
        billMapper.updateById(bill);

        BillItem discountItem = new BillItem();
        discountItem.setBillId(dto.getBillId());
        discountItem.setItemType("DISCOUNT");
        discountItem.setFeeItem("账单减免");
        discountItem.setAmount(dto.getDiscountAmount());
        discountItem.setDescription(dto.getDiscountReason());
        discountItem.setCreateTime(LocalDateTime.now());
        discountItem.setCreateUser(currentUserId);
        discountItem.setUpdateTime(LocalDateTime.now());
        discountItem.setUpdateUser(currentUserId);
        billItemMapper.insert(discountItem);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOtherFee(BillOtherFeeDTO dto, Long currentUserId) {
        Bill bill = billMapper.selectById(dto.getBillId());
        if (bill == null) {
            throw new BusinessException("账单不存在");
        }

        if (STATUS_CONFIRMED.equals(bill.getStatus())) {
            throw new BusinessException("已确认的账单不能添加费用");
        }

        BigDecimal currentOtherFee = NumberUtil.nullToZero(bill.getOtherFeeReceivable());
        BigDecimal newOtherFee = currentOtherFee.add(dto.getAmount());

        bill.setOtherFeeReceivable(newOtherFee);
        bill.setActualAmount(calculateActualAmount(bill));
        bill.setDueAmount(calculateDueAmount(bill));
        bill.setUpdateTime(LocalDateTime.now());
        bill.setUpdateUser(currentUserId);
        billMapper.updateById(bill);

        BillItem otherFeeItem = new BillItem();
        otherFeeItem.setBillId(dto.getBillId());
        otherFeeItem.setItemType("OTHER_FEE");
        otherFeeItem.setFeeItem(dto.getFeeItem());
        otherFeeItem.setAmount(dto.getAmount());
        otherFeeItem.setDescription(dto.getDescription());
        otherFeeItem.setCreateTime(LocalDateTime.now());
        otherFeeItem.setCreateUser(currentUserId);
        otherFeeItem.setUpdateTime(LocalDateTime.now());
        otherFeeItem.setUpdateUser(currentUserId);
        billItemMapper.insert(otherFeeItem);

        return true;
    }

    @Override
    public byte[] exportBills(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            throw new BusinessException("请选择要导出的账单");
        }

        List<Bill> bills = billMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(bills)) {
            throw new BusinessException("未找到要导出的账单");
        }

        List<BillVO> billVOList = bills.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("账单列表");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"账单编号", "幼儿姓名", "班级", "账单月份", "保教费应收", "伙食费应收",
                    "其他费用应收", "保教费应退", "伙食费应退", "减免金额", "实际应交", "账单状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (BillVO vo : billVOList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(vo.getBillNo() != null ? vo.getBillNo() : "");
                row.createCell(1).setCellValue(vo.getChildName() != null ? vo.getChildName() : "");
                row.createCell(2).setCellValue(vo.getClassName() != null ? vo.getClassName() : "");
                row.createCell(3).setCellValue(vo.getBillMonth() != null ? vo.getBillMonth() : "");
                row.createCell(4).setCellValue(vo.getEducationFeeReceivable() != null ? vo.getEducationFeeReceivable().doubleValue() : 0);
                row.createCell(5).setCellValue(vo.getMealFeeReceivable() != null ? vo.getMealFeeReceivable().doubleValue() : 0);
                row.createCell(6).setCellValue(vo.getOtherFeeReceivable() != null ? vo.getOtherFeeReceivable().doubleValue() : 0);
                row.createCell(7).setCellValue(vo.getEducationFeeRefund() != null ? vo.getEducationFeeRefund().doubleValue() : 0);
                row.createCell(8).setCellValue(vo.getMealFeeRefund() != null ? vo.getMealFeeRefund().doubleValue() : 0);
                row.createCell(9).setCellValue(vo.getDiscountAmount() != null ? vo.getDiscountAmount().doubleValue() : 0);
                row.createCell(10).setCellValue(vo.getActualAmount() != null ? vo.getActualAmount().doubleValue() : 0);
                row.createCell(11).setCellValue(vo.getStatusDesc() != null ? vo.getStatusDesc() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new BusinessException("导出账单失败：" + e.getMessage());
        }
    }

    private void doGenerateBill(Child child, Contract contract, Integer year, Integer month,
                                Long currentUserId) {
        doGenerateBill(child, contract, year, month, currentUserId, null);
    }

    private void doGenerateBill(Child child, Contract contract, Integer year, Integer month,
                                Long currentUserId, Long existBillId) {
        AttendanceStatisticsVO attendance = getAttendanceStatistics(child.getId(), year, month);

        BigDecimal educationFee = billCalculateService.calculateEducationFee(contract, year, month);
        BigDecimal mealFee = billCalculateService.calculateMealFee(contract, year, month);
        BigDecimal otherFee = billCalculateService.calculateOtherFee(contract, year, month);

        BigDecimal educationRefund = billCalculateService.calculateEducationRefundByAge(contract, educationFee, attendance, year, month);
        BigDecimal mealRefund = billCalculateService.calculateMealRefund(mealFee, attendance, year, month);

        Bill bill;
        if (existBillId != null) {
            bill = billMapper.selectById(existBillId);
        } else {
            bill = new Bill();
            bill.setBillNo(generateBillNo());
            bill.setChildId(child.getId());
            bill.setContractId(contract.getId());
            bill.setBillMonth(String.format("%d-%02d", year, month));
            bill.setStatus(STATUS_PENDING);
            bill.setCreateTime(LocalDateTime.now());
            bill.setCreateUser(currentUserId);
        }

        bill.setEducationFeeReceivable(educationFee);
        bill.setMealFeeReceivable(mealFee);
        bill.setOtherFeeReceivable(otherFee);
        bill.setEducationFeeRefund(educationRefund);
        bill.setMealFeeRefund(mealRefund);
        bill.setDiscountAmount(BigDecimal.ZERO);
        bill.setActualAmount(calculateActualAmount(bill));
        bill.setDueAmount(calculateDueAmount(bill));
        bill.setUpdateTime(LocalDateTime.now());
        bill.setUpdateUser(currentUserId);

        if (existBillId != null) {
            billMapper.updateById(bill);
        } else {
            billMapper.insert(bill);
        }

        saveBillItems(bill.getId(), educationFee, mealFee, otherFee, educationRefund, mealRefund, currentUserId);
    }

    private BigDecimal calculateActualAmount(Bill bill) {
        BigDecimal receivable = BigDecimal.ZERO;
        receivable = receivable.add(NumberUtil.nullToZero(bill.getEducationFeeReceivable()));
        receivable = receivable.add(NumberUtil.nullToZero(bill.getMealFeeReceivable()));
        receivable = receivable.add(NumberUtil.nullToZero(bill.getOtherFeeReceivable()));

        BigDecimal refund = BigDecimal.ZERO;
        refund = refund.add(NumberUtil.nullToZero(bill.getEducationFeeRefund()));
        refund = refund.add(NumberUtil.nullToZero(bill.getMealFeeRefund()));

        BigDecimal discount = NumberUtil.nullToZero(bill.getDiscountAmount());

        return receivable.subtract(refund).subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDueAmount(Bill bill) {
        BigDecimal actualAmount = bill.getActualAmount() != null ? bill.getActualAmount() : BigDecimal.ZERO;
        
        BigDecimal educationBalance = balanceService.getBalance(bill.getChildId(), "EDUCATION");
        BigDecimal mealBalance = balanceService.getBalance(bill.getChildId(), "MEAL");
        BigDecimal otherBalance = balanceService.getBalance(bill.getChildId(), "OTHER");

        BigDecimal totalBalance = BigDecimal.ZERO;
        totalBalance = totalBalance.add(educationBalance != null ? educationBalance : BigDecimal.ZERO);
        totalBalance = totalBalance.add(mealBalance != null ? mealBalance : BigDecimal.ZERO);
        totalBalance = totalBalance.add(otherBalance != null ? otherBalance : BigDecimal.ZERO);
        
        BigDecimal dueAmount = actualAmount.subtract(totalBalance);
        
        return dueAmount.compareTo(BigDecimal.ZERO) > 0 ? dueAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    private void saveBillItems(Long billId, BigDecimal educationFee, BigDecimal mealFee,
                               BigDecimal otherFee, BigDecimal educationRefund, BigDecimal mealRefund,
                               Long currentUserId) {
        LocalDateTime now = LocalDateTime.now();

        if (educationFee != null && educationFee.compareTo(BigDecimal.ZERO) > 0) {
            BillItem item = new BillItem();
            item.setBillId(billId);
            item.setItemType("EDUCATION");
            item.setFeeItem("保教费");
            item.setAmount(educationFee);
            item.setDescription("月度保教费");
            item.setCreateTime(now);
            item.setCreateUser(currentUserId);
            item.setUpdateTime(now);
            item.setUpdateUser(currentUserId);
            billItemMapper.insert(item);
        }

        if (mealFee != null && mealFee.compareTo(BigDecimal.ZERO) > 0) {
            BillItem item = new BillItem();
            item.setBillId(billId);
            item.setItemType("MEAL");
            item.setFeeItem("伙食费");
            item.setAmount(mealFee);
            item.setDescription("月度伙食费");
            item.setCreateTime(now);
            item.setCreateUser(currentUserId);
            item.setUpdateTime(now);
            item.setUpdateUser(currentUserId);
            billItemMapper.insert(item);
        }

        if (otherFee != null && otherFee.compareTo(BigDecimal.ZERO) > 0) {
            BillItem item = new BillItem();
            item.setBillId(billId);
            item.setItemType("OTHER_FEE");
            item.setFeeItem("其他费用");
            item.setAmount(otherFee);
            item.setDescription("其他费用");
            item.setCreateTime(now);
            item.setCreateUser(currentUserId);
            item.setUpdateTime(now);
            item.setUpdateUser(currentUserId);
            billItemMapper.insert(item);
        }

        if (educationRefund != null && educationRefund.compareTo(BigDecimal.ZERO) > 0) {
            BillItem item = new BillItem();
            item.setBillId(billId);
            item.setItemType("EDUCATION_REFUND");
            item.setFeeItem("保教费退费");
            item.setAmount(educationRefund);
            item.setDescription("请假/病假退费");
            item.setCreateTime(now);
            item.setCreateUser(currentUserId);
            item.setUpdateTime(now);
            item.setUpdateUser(currentUserId);
            billItemMapper.insert(item);
        }

        if (mealRefund != null && mealRefund.compareTo(BigDecimal.ZERO) > 0) {
            BillItem item = new BillItem();
            item.setBillId(billId);
            item.setItemType("MEAL_REFUND");
            item.setFeeItem("伙食费退费");
            item.setAmount(mealRefund);
            item.setDescription("未到园退费");
            item.setCreateTime(now);
            item.setCreateUser(currentUserId);
            item.setUpdateTime(now);
            item.setUpdateUser(currentUserId);
            billItemMapper.insert(item);
        }
    }

    private ContractVO getActiveContract(Long childId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contract::getChildId, childId)
                .eq(Contract::getStatus, 1)
                .le(Contract::getStartDate, monthEnd)
                .and(w -> w.isNull(Contract::getEndDate)
                        .or()
                        .ge(Contract::getEndDate, monthStart))
                .orderByDesc(Contract::getCreateTime)
                .last("LIMIT 1");

        Contract contract = contractMapper.selectOne(queryWrapper);
        if (contract == null) {
            return null;
        }

        ContractVO vo = new ContractVO();
        BeanUtils.copyProperties(contract, vo);
        vo.setId(contract.getId().toString());
        return vo;
    }

    private boolean isFirstBill(Long childId, Long contractId) {
        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Bill::getChildId, childId)
                .eq(Bill::getContractId, contractId);
        Long count = billMapper.selectCount(queryWrapper);
        return count == null || count == 0;
    }

    private AttendanceStatisticsVO getAttendanceStatistics(Long childId, Integer year, Integer month) {
        AttendanceStatisticsVO vo = new AttendanceStatisticsVO();
        vo.setChildId(childId);
        vo.setYear(year);
        vo.setMonth(month);

        int totalDays = billCalculateService.getWorkingDays(year, month);
        vo.setTotalDays(totalDays);

        Integer normalDays = attendanceMapper.countByChildAndMonth(childId, year, month, 1);
        Integer leaveDays = attendanceMapper.countByChildAndMonth(childId, year, month, 2);
        Integer sickDays = attendanceMapper.countByChildAndMonth(childId, year, month, 3);
        Integer absentDays = attendanceMapper.countByChildAndMonth(childId, year, month, 4);

        vo.setNormalDays(normalDays != null ? normalDays : 0);
        vo.setLeaveDays(leaveDays != null ? leaveDays : 0);
        vo.setSickDays(sickDays != null ? sickDays : 0);
        vo.setAbsentDays(absentDays != null ? absentDays : 0);

        return vo;
    }

    private String generateBillNo() {
        String dateStr = LocalDate.now().toString().replace("-", "");
        long sequence = SnowflakeIdGenerator.generateId() % 10000;
        return String.format("ZD%s%04d", dateStr, sequence);
    }

    private BillVO convertToVO(Bill bill) {
        BillVO vo = new BillVO();
        BeanUtils.copyProperties(bill, vo);
        vo.setId(bill.getId().toString());
        vo.setContractId(bill.getContractId().toString());
        vo.setStatusDesc(STATUS_DESC_MAP.get(bill.getStatus()));

        Child child = childMapper.selectById(bill.getChildId());
        if (child != null) {
            vo.setChildName(child.getName());
            vo.setChildGender(child.getGender() != null ? child.getGender().toString() : null);
            vo.setChildBirthDate(child.getBirthDate());

            if (child.getClassId() != null) {
                Clazz clazz = clazzMapper.selectById(child.getClassId());
                if (clazz != null) {
                    vo.setClassName(clazz.getClassName());
                }
            }
        }

        Contract contract = contractMapper.selectById(bill.getContractId());
        if (contract != null) {
            vo.setContractNo(contract.getContractNo());
            vo.setContractName(contract.getContractName());
        }

        if (bill.getConfirmUser() != null) {
            User user = userMapper.selectById(bill.getConfirmUser());
            if (user != null) {
                vo.setConfirmUserName(user.getRealName());
            }
        }

        return vo;
    }

    @Override
    public List<BillVO.BillItemVO> getBillItems(Long billId) {
        List<BillItem> items = billItemMapper.selectByBillId(billId);
        if (CollUtil.isEmpty(items)) {
            return new ArrayList<>();
        }

        return items.stream().map(item -> {
            BillVO.BillItemVO vo = new BillVO.BillItemVO();
            BeanUtils.copyProperties(item, vo);
            vo.setItemTypeDesc(ITEM_TYPE_DESC_MAP.get(item.getItemType()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchGenerateBill(BillBatchGenerateDTO dto) {
        String[] parts = dto.getBillMonth().split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        log.info("开始批量生成{}年{}月账单", year, month);

        LambdaQueryWrapper<Child> childQueryWrapper = new LambdaQueryWrapper<>();
        childQueryWrapper.eq(Child::getStatus, 1);
        if (dto.getClassId() != null) {
            childQueryWrapper.eq(Child::getClassId, dto.getClassId());
        }
        List<Child> children = childMapper.selectList(childQueryWrapper);

        int count = 0;
        for (Child child : children) {
            try {
                String billMonth = String.format("%d-%02d", year, month);
                Bill existBill = billMapper.selectByChildIdAndMonth(child.getId(), billMonth);
                if (existBill != null) {
                    log.info("幼儿{}当月账单已存在，跳过", child.getName());
                    continue;
                }

                ContractVO contractVO = getActiveContract(child.getId(), year, month);
                if (contractVO == null) {
                    log.info("幼儿{}当月无生效合同，跳过", child.getName());
                    continue;
                }

                Contract contract = contractMapper.selectById(contractVO.getId());

                doGenerateBill(child, contract, year, month, null);
                count++;
            } catch (Exception e) {
                log.error("生成幼儿{}账单失败: {}", child.getName(), e.getMessage(), e);
            }
        }

        log.info("账单批量生成完成，共生成{}条账单", count);
        return count;
    }

}
