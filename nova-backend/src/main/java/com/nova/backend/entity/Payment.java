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
 * 收费记录实体类
 *
 * @author Nova
 */
@Data
@TableName("t_payment")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 收据编号
     */
    private String receiptNo;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 费用类型：EDUCATION-保教费, MEAL-伙食费, OTHER-其他费用
     */
    private String paymentType;

    /**
     * 费用项目名称
     */
    private String feeItem;

    /**
     * 收费金额
     */
    private BigDecimal amount;

    /**
     * 支付方式：WECHAT-微信, ALIPAY-支付宝, CASH-现金, TRANSFER-转账
     */
    private String paymentMethod;

    /**
     * 状态：VALID-有效, VOIDED-已作废
     */
    private String status;

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
