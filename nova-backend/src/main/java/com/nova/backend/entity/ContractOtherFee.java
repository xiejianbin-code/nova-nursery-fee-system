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
 * 合同其他费用实体类
 *
 * @author Nova
 */
@Data
@TableName("t_contract_other_fee")
public class ContractOtherFee implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 费用编码
     */
    private String feeCode;

    /**
     * 费用名称
     */
    private String feeName;

    /**
     * 收费周期：ONCE-一次性, MONTHLY-按月, SEMESTER-按学期
     */
    private String chargeCycle;

    /**
     * 收费标准
     */
    private BigDecimal feeStandard;

    /**
     * 状态：0-停用, 1-启用
     */
    private Integer status;

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
