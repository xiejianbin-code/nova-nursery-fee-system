package com.nova.backend.service;

import com.nova.backend.entity.Contract;
import com.nova.backend.vo.AttendanceStatisticsVO;

import java.math.BigDecimal;

/**
 * 账单核算服务接口
 *
 * @author Nova
 */
public interface BillCalculateService {

    /**
     * 核算保教费应收
     * 保教费应收 = 合同约定月保教费标准 ÷ 当月工作日天数 × 该段工作日天数（跨月龄分段核算）
     *
     * @param contract   合同信息
     * @param year       年份
     * @param month      月份
     * @return 保教费应收金额
     */
    BigDecimal calculateEducationFee(Contract contract, Integer year, Integer month);

    /**
     * 核算伙食费应收
     * 伙食费应收 = 合同约定月伙食费标准
     *
     * @param contract 合同信息
     * @param year     年份
     * @param month    月份
     * @return 伙食费应收金额
     */
    BigDecimal calculateMealFee(Contract contract, Integer year, Integer month);

    /**
     * 核算其他费用应收
     * 一次性费用(ONCE)：幼儿历史首次生成账单时计提
     * 按月费用(MONTHLY)：仅当月保教费应收>0时计提
     * 按学期费用(SEMESTER)：合同服务开始日期所在的自然月计提
     *
     * @param contract    合同信息
     * @param year        年份
     * @param month       月份
     * @return 其他费用应收金额
     */
    BigDecimal calculateOtherFee(Contract contract, Integer year, Integer month);

    /**
     * 核算应退保教费
     * 应退保教费 = (符合规则的事假天数 + 符合规则的病假天数) × 日保教费单价
     * 事假/病假天数超过配置的起算天数才退费
     *
     * @param educationFee 保教费应收
     * @param attendance   出勤统计
     * @param year         年份
     * @param month        月份
     * @return 应退保教费金额
     */
    BigDecimal calculateEducationRefund(BigDecimal educationFee, AttendanceStatisticsVO attendance, Integer year, Integer month);

    /**
     * 核算应退保教费（跨月龄分段核算）
     * 按请假/病假日期计算对应月龄的保教费标准
     *
     * @param contract      合同信息
     * @param educationFee  保教费应收
     * @param attendance    出勤统计
     * @param year          年份
     * @param month         月份
     * @return 应退保教费金额
     */
    BigDecimal calculateEducationRefundByAge(Contract contract, BigDecimal educationFee, 
            AttendanceStatisticsVO attendance, Integer year, Integer month);

    /**
     * 核算应退伙食费
     * 应退伙食费 = 未到园天数 × 日伙食费单价
     *
     * @param mealFee    伙食费应收
     * @param attendance 出勤统计
     * @param year       年份
     * @param month      月份
     * @return 应退伙食费金额
     */
    BigDecimal calculateMealRefund(BigDecimal mealFee, AttendanceStatisticsVO attendance, Integer year, Integer month);

    /**
     * 获取当月工作日天数
     *
     * @param year  年份
     * @param month 月份
     * @return 工作日天数
     */
    int getWorkingDays(Integer year, Integer month);

    /**
     * 获取幼儿某月龄对应的保教费标准
     *
     * @param contractId 合同ID
     * @param ageMonths  月龄
     * @return 保教费标准
     */
    BigDecimal getEducationFeeByAge(Long contractId, Integer ageMonths);

    /**
     * 获取幼儿某月龄对应的伙食费标准
     *
     * @param contractId 合同ID
     * @param ageMonths  月龄
     * @return 伙食费标准
     */
    BigDecimal getMealFeeByAge(Long contractId, Integer ageMonths);

}
