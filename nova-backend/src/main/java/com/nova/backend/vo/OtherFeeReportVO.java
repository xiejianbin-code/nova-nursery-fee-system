package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 其他费用明细报表响应对象
 *
 * @author Nova
 */
@Data
public class OtherFeeReportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 费用编码
     */
    private String feeCode;

    /**
     * 费用名称
     */
    private String feeName;

    /**
     * 收费周期：ONCE-一次性, MONTHLY-按月, SEMESTER-按学期
     */
    private String chargeCycle;

    /**
     * 收费周期名称
     */
    private String chargeCycleName;

    /**
     * 应收金额
     */
    private BigDecimal receivableAmount;

    /**
     * 已收金额
     */
    private BigDecimal paidAmount;

    /**
     * 未收金额
     */
    private BigDecimal unpaidAmount;

    /**
     * 涉及幼儿数
     */
    private Integer childCount;

    /**
     * 已缴费幼儿数
     */
    private Integer paidChildCount;
}
