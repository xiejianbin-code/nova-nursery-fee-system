package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 月度收入汇总报表响应对象
 *
 * @author Nova
 */
@Data
public class MonthlyIncomeReportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 保教费应收总额
     */
    private BigDecimal educationFeeTotal;

    /**
     * 伙食费应收总额
     */
    private BigDecimal mealFeeTotal;

    /**
     * 其他费用应收总额
     */
    private BigDecimal otherFeeTotal;

    /**
     * 已收金额
     */
    private BigDecimal paidAmount;

    /**
     * 未收金额
     */
    private BigDecimal unpaidAmount;

    /**
     * 减免金额
     */
    private BigDecimal discountAmount;

    /**
     * 应退金额
     */
    private BigDecimal refundAmount;

    /**
     * 实际收入总额
     */
    private BigDecimal totalIncome;

    /**
     * 在园幼儿数
     */
    private Integer totalChildren;

    /**
     * 已缴费幼儿数
     */
    private Integer paidChildren;
}
