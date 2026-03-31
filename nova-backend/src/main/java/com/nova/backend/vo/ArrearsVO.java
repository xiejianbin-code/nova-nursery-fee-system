package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 欠费名单响应对象
 *
 * @author Nova
 */
@Data
public class ArrearsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 幼儿姓名
     */
    private String childName;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 家长电话
     */
    private String parentPhone;

    /**
     * 欠费金额
     */
    private BigDecimal arrearsAmount;

    /**
     * 逾期天数
     */
    private Integer overdueDays;

    /**
     * 欠费账单月份列表
     */
    private String billMonths;
}
