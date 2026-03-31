package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

/**
 * 添加其他费用DTO
 *
 * @author Nova
 */
@Data
public class BillOtherFeeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账单ID
     */
    @NotNull(message = "账单ID不能为空")
    private Long billId;

    /**
     * 费用项目名称
     */
    @NotBlank(message = "费用项目名称不能为空")
    private String feeItem;

    /**
     * 金额
     */
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    /**
     * 说明
     */
    private String description;

}
