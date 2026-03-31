package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 合同查询条件DTO
 *
 * @author Nova
 */
@Data
public class ContractQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 课程类型：MONTHLY-按月, SEMESTER-按学期
     */
    private String courseType;

    /**
     * 合同状态：1-生效中, 2-已到期, 3-已变更, 4-已终止
     */
    private Integer status;

    /**
     * 合同开始日期-起始
     */
    private LocalDate startDateBegin;

    /**
     * 合同开始日期-结束
     */
    private LocalDate startDateEnd;

    /**
     * 当前页码
     */
    private Long page;

    /**
     * 每页数量
     */
    private Long size;
}
