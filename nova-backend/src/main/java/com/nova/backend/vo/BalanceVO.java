package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 余额响应对象
 *
 * @author Nova
 */
@Data
public class BalanceVO implements Serializable {

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
     * 账户余额列表
     */
    private List<AccountBalance> accounts;

    /**
     * 账户余额详情
     */
    @Data
    public static class AccountBalance implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 账户类型：EDUCATION-保教费, MEAL-伙食费, OTHER-其他费用
         */
        private String accountType;

        /**
         * 账户类型名称
         */
        private String accountTypeName;

        /**
         * 账户余额
         */
        private BigDecimal balance;
    }
}
