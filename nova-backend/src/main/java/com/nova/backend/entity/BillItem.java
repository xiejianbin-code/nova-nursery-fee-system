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
 * 账单明细实体类
 *
 * @author Nova
 */
@Data
@TableName("t_bill_item")
public class BillItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账单ID
     */
    private Long billId;

    /**
     * 明细类型：EDUCATION-保教费, MEAL-伙食费, OTHER_FEE-其他费用, EDUCATION_REFUND-保教费退费, MEAL_REFUND-伙食费退费, DISCOUNT-减免
     */
    private String itemType;

    /**
     * 费用项目名称
     */
    private String feeItem;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 说明
     */
    private String description;

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
     * 删除标记：0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;

}
