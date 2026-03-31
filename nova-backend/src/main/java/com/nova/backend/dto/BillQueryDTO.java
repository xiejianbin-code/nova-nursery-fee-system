package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 账单查询条件DTO
 *
 * @author Nova
 */
@Data
public class BillQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账单编号
     */
    private String billNo;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 幼儿姓名（模糊查询）
     */
    private String childName;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 账单月份（格式：2024-01）
     */
    private String billMonth;

    /**
     * 账单状态：PENDING-待确认, CONFIRMED-已确认
     */
    private String status;

    /**
     * 当前页码
     */
    private Long page;

    /**
     * 每页数量
     */
    private Long size;

}
