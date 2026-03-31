package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 余额变动记录响应对象
 *
 * @author Nova
 */
@Data
public class BalanceRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 幼儿姓名
     */
    private String childName;

    /**
     * 账户类型：EDUCATION-保教费, MEAL-伙食费, OTHER-其他费用
     */
    private String accountType;

    /**
     * 账户类型名称
     */
    private String accountTypeName;

    /**
     * 变动类型：INCOME-收入, EXPENSE-支出
     */
    private String changeType;

    /**
     * 变动类型名称
     */
    private String changeTypeName;

    /**
     * 变动金额
     */
    private BigDecimal amount;

    /**
     * 变动后余额
     */
    private BigDecimal afterBalance;

    /**
     * 关联单号（收费单号/退费单号等）
     */
    private String relatedNo;

    /**
     * 费用项目名称
     */
    private String feeItem;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long createUser;
}
