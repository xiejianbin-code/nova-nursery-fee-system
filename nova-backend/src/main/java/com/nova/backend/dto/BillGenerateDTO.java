package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;

/**
 * 生成账单DTO
 *
 * @author Nova
 */
@Data
public class BillGenerateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幼儿ID
     */
    @NotNull(message = "幼儿ID不能为空")
    private Long childId;

    /**
     * 年份
     */
    @NotNull(message = "年份不能为空")
    private Integer year;

    /**
     * 月份
     */
    @NotNull(message = "月份不能为空")
    private Integer month;

}
