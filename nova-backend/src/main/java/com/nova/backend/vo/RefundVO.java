package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退费记录响应对象
 *
 * @author Nova
 */
@Data
public class RefundVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 退费编号
     */
    private String refundNo;

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
     * 费用项目名称
     */
    private String feeItem;

    /**
     * 退费金额
     */
    private BigDecimal amount;

    /**
     * 退费原因
     */
    private String reason;

    /**
     * 状态：PENDING-待处理, COMPLETED-已完成, CANCELLED-已取消
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private Long updateUser;
}
