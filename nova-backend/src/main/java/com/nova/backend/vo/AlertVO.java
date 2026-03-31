package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预警记录响应对象
 *
 * @author Nova
 */
@Data
public class AlertVO implements Serializable {

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
     * 班级名称
     */
    private String className;

    /**
     * 预警类型：CONTRACT_EXPIRE-合同到期, FEE_EXPIRE-保教费到期, OVERDUE-逾期未缴费, OTHER_FEE_LOW-其他费用余额不足
     */
    private String alertType;

    /**
     * 预警类型名称
     */
    private String alertTypeName;

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
     * 处理状态名称
     */
    private String handleStatusName;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 处理人
     */
    private Long handleUser;

    /**
     * 处理人姓名
     */
    private String handleUserName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
