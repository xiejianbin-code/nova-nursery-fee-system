package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

/**
 * 批量生成账单DTO
 *
 * @author Nova
 */
@Data
public class BillBatchGenerateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账单月份（格式：2024-01）
     */
    @NotBlank(message = "账单月份不能为空")
    private String billMonth;

    /**
     * 班级ID（可选，不传则生成所有班级）
     */
    private Long classId;

}
