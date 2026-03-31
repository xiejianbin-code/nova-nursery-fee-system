package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 退费查询条件DTO
 *
 * @author Nova
 */
@Data
public class RefundQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 状态：PENDING-待处理, COMPLETED-已完成, CANCELLED-已取消
     */
    private String status;

    /**
     * 退费日期-起始
     */
    private LocalDate refundDateBegin;

    /**
     * 退费日期-结束
     */
    private LocalDate refundDateEnd;

    /**
     * 当前页码
     */
    private Long page;

    /**
     * 每页数量
     */
    private Long size;
}
