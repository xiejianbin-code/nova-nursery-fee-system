package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 幼儿余额统计报表响应对象
 *
 * @author Nova
 */
@Data
public class BalanceReportVO implements Serializable {

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
     * 保教费余额
     */
    private BigDecimal educationBalance;

    /**
     * 伙食费余额
     */
    private BigDecimal mealBalance;

    /**
     * 其他费用余额
     */
    private BigDecimal otherBalance;

    /**
     * 总余额
     */
    private BigDecimal totalBalance;
}
