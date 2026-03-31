package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预警查询条件DTO
 *
 * @author Nova
 */
@Data
public class AlertQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 预警类型：CONTRACT_EXPIRE-合同到期, FEE_EXPIRE-保教费到期, OVERDUE-逾期未缴费, OTHER_FEE_LOW-其他费用余额不足
     */
    private String alertType;

    /**
     * 处理状态：PENDING-待处理, HANDLED-已处理
     */
    private String handleStatus;

    /**
     * 触发时间-起始
     */
    private LocalDateTime triggerTimeBegin;

    /**
     * 触发时间-结束
     */
    private LocalDateTime triggerTimeEnd;

    /**
     * 当前页码
     */
    private Long page;

    /**
     * 每页数量
     */
    private Long size;
}
