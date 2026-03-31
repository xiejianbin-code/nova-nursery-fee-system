package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 班级收费统计报表响应对象
 *
 * @author Nova
 */
@Data
public class ClassFeeReportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 在册幼儿数
     */
    private Integer studentCount;

    /**
     * 已缴费幼儿数
     */
    private Integer paidCount;

    /**
     * 未缴费幼儿数
     */
    private Integer unpaidCount;

    /**
     * 应收总金额
     */
    private BigDecimal totalAmount;

    /**
     * 已收金额
     */
    private BigDecimal paidAmount;

    /**
     * 未收金额
     */
    private BigDecimal unpaidAmount;

    /**
     * 人均应收
     */
    private BigDecimal avgAmount;

    /**
     * 收费率
     */
    private BigDecimal paidRate;
}
