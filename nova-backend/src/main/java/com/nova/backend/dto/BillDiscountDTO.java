package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

/**
 * 账单减免DTO
 *
 * @author Nova
 */
@Data
public class BillDiscountDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账单ID
     */
    @NotNull(message = "账单ID不能为空")
    private Long billId;

    /**
     * 减免金额
     */
    @NotNull(message = "减免金额不能为空")
    @DecimalMin(value = "0.01", message = "减免金额必须大于0")
    private BigDecimal discountAmount;

    /**
     * 减免原因
     */
    @NotNull(message = "减免原因不能为空")
    private String discountReason;

}
