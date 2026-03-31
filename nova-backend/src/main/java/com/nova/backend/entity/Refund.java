package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退费记录实体类
 *
 * @author Nova
 */
@Data
@TableName("t_refund")
public class Refund implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
