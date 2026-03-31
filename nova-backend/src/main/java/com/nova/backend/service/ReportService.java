package com.nova.backend.service;

import com.nova.backend.vo.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 报表服务接口
 *
 * @author Nova
 */
public interface ReportService {

    /**
     * 获取全园月度收入汇总报表
     *
     * @param year  年份
     * @param month 月份
     * @return 月度收入汇总报表
     */
    MonthlyIncomeReportVO getMonthlyIncomeReport(Integer year, Integer month);

    /**
     * 获取班级收费统计报表
     *
     * @param classId 班级ID，为空则查询所有班级
     * @param year    年份
     * @param month   月份
     * @return 班级收费统计报表列表
     */
    List<ClassFeeReportVO> getClassFeeReport(Long classId, Integer year, Integer month);

    /**
     * 获取全园欠费名单报表
     *
     * @return 欠费名单列表
     */
    List<ArrearsVO> getArrearsList();

    /**
     * 获取幼儿余额统计报表
     *
     * @param classId 班级ID，为空则查询所有班级
     * @return 幼儿余额统计列表
     */
    List<BalanceReportVO> getBalanceReport(Long classId);

    /**
     * 获取合同管理统计报表
     *
     * @return 合同统计报表
     */
    ContractReportVO getContractReport();

    /**
     * 获取其他费用收支明细报表
     *
     * @param year  年份
     * @param month 月份
     * @return 其他费用明细列表
     */
    List<OtherFeeReportVO> getOtherFeeReport(Integer year, Integer month);

    /**
     * 导出报表
     *
     * @param type     报表类型
     * @param year     年份
     * @param month    月份
     * @param classId  班级ID
     * @param response HTTP响应
     */
    void exportReport(String type, Integer year, Integer month, Long classId, HttpServletResponse response);
}
