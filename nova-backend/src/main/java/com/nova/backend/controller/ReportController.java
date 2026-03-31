package com.nova.backend.controller;

import com.nova.backend.common.Result;
import com.nova.backend.service.ReportService;
import com.nova.backend.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

/**
 * 报表管理控制器
 *
 * @author Nova
 */
@Tag(name = "报表管理")
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 获取月度收入汇总报表
     */
    @Operation(summary = "获取月度收入汇总报表")
    @GetMapping("/monthly-income")
    public Result<MonthlyIncomeReportVO> getMonthlyIncomeReport(
            @Parameter(description = "年份") @RequestParam(defaultValue = "") Integer year,
            @Parameter(description = "月份") @RequestParam(defaultValue = "") Integer month) {
        if (year == null || month == null) {
            YearMonth currentMonth = YearMonth.now();
            year = currentMonth.getYear();
            month = currentMonth.getMonthValue();
        }
        MonthlyIncomeReportVO report = reportService.getMonthlyIncomeReport(year, month);
        return Result.success(report);
    }

    /**
     * 获取班级收费统计报表
     */
    @Operation(summary = "获取班级收费统计报表")
    @GetMapping("/class-fee")
    public Result<List<ClassFeeReportVO>> getClassFeeReport(
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份") @RequestParam(required = false) Integer month) {
        if (year == null || month == null) {
            YearMonth currentMonth = YearMonth.now();
            year = currentMonth.getYear();
            month = currentMonth.getMonthValue();
        }
        List<ClassFeeReportVO> reports = reportService.getClassFeeReport(classId, year, month);
        return Result.success(reports);
    }

    /**
     * 获取欠费名单报表
     */
    @Operation(summary = "获取欠费名单报表")
    @GetMapping("/arrears")
    public Result<List<ArrearsVO>> getArrearsList() {
        List<ArrearsVO> arrearsList = reportService.getArrearsList();
        return Result.success(arrearsList);
    }

    /**
     * 获取幼儿余额统计报表
     */
    @Operation(summary = "获取幼儿余额统计报表")
    @GetMapping("/balance")
    public Result<List<BalanceReportVO>> getBalanceReport(
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId) {
        List<BalanceReportVO> reports = reportService.getBalanceReport(classId);
        return Result.success(reports);
    }

    /**
     * 获取合同统计报表
     */
    @Operation(summary = "获取合同统计报表")
    @GetMapping("/contract")
    public Result<ContractReportVO> getContractReport() {
        ContractReportVO report = reportService.getContractReport();
        return Result.success(report);
    }

    /**
     * 获取其他费用明细报表
     */
    @Operation(summary = "获取其他费用明细报表")
    @GetMapping("/other-fee")
    public Result<List<OtherFeeReportVO>> getOtherFeeReport(
            @Parameter(description = "年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份") @RequestParam(required = false) Integer month) {
        if (year == null || month == null) {
            YearMonth currentMonth = YearMonth.now();
            year = currentMonth.getYear();
            month = currentMonth.getMonthValue();
        }
        List<OtherFeeReportVO> reports = reportService.getOtherFeeReport(year, month);
        return Result.success(reports);
    }

    /**
     * 导出报表
     */
    @Operation(summary = "导出报表")
    @GetMapping("/export")
    public void exportReport(
            @Parameter(description = "报表类型：monthly-income/class-fee/arrears/balance/contract/other-fee") @RequestParam String type,
            @Parameter(description = "年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份") @RequestParam(required = false) Integer month,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            HttpServletResponse response) {
        if (year == null || month == null) {
            YearMonth currentMonth = YearMonth.now();
            year = currentMonth.getYear();
            month = currentMonth.getMonthValue();
        }
        reportService.exportReport(type, year, month, classId, response);
    }
}
