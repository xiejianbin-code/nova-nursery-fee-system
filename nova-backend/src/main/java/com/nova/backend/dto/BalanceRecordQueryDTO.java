package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 余额变动查询条件DTO
 *
 * @author Nova
 */
@Data
public class BalanceRecordQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 账户类型：EDUCATION-保教费, MEAL-伙食费, OTHER-其他费用
     */
    private String accountType;

    /**
     * 变动类型：INCOME-收入, EXPENSE-支出
     */
    private String changeType;

    /**
     * 当前页码
     */
    private Long page;

    /**
     * 每页数量
     */
    private Long size;
}
