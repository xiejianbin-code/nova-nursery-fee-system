package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预警记录实体类
 *
 * @author Nova
 */
@Data
@TableName("t_alert")
public class Alert implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 预警类型：CONTRACT_EXPIRE-合同到期提醒, EDUCATION_FEE_EXPIRE-保教费到期提醒, MEAL_FEE_EXPIRE-伙食费到期提醒, OVERDUE-逾期未缴费提醒, EDUCATION_FEE_LOW-保教费余额不足提醒, MEAL_FEE_LOW-伙食费余额不足提醒, OTHER_FEE_LOW-其他费用余额不足提醒
     */
    private String alertType;

    /**
     * 预警内容
     */
    private String alertContent;

    /**
     * 触发时间
     */
    private LocalDateTime triggerTime;

    /**
     * 处理状态：PENDING-待处理, HANDLED-已处理
     */
    private String handleStatus;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 处理人
     */
    private Long handleUser;

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
     * 删除标记：0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
