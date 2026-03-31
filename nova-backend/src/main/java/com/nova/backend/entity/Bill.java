package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账单实体类
 *
 * @author Nova
 */
@Data
@TableName("t_bill")
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账单编号
     */
    private String billNo;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 账单月份（格式：2024-01）
     */
    private String billMonth;

    /**
     * 保教费应收
     */
    private BigDecimal educationFeeReceivable;

    /**
     * 伙食费应收
     */
    private BigDecimal mealFeeReceivable;

    /**
     * 其他费用应收
     */
    private BigDecimal otherFeeReceivable;

    /**
     * 保教费应退
     */
    private BigDecimal educationFeeRefund;

    /**
     * 伙食费应退
     */
    private BigDecimal mealFeeRefund;

    /**
     * 减免金额
     */
    private BigDecimal discountAmount;

    /**
     * 实际应交金额
     */
    private BigDecimal actualAmount;

    /**
     * 实际需缴金额（扣除余额后）
     */
    private BigDecimal dueAmount;

    /**
     * 账单状态：PENDING-待确认, CONFIRMED-已确认
     */
    private String status;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 确认人
     */
    private Long confirmUser;

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
