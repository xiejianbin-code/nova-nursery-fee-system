package com.nova.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 合同年龄段收费明细数据传输对象
 *
 * @author Nova
 */
@Data
public class ContractFeeItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（更新时使用）
     */
    private Long id;

    /**
     * 年龄段：4-12月/13-18月/19-24月/25-36月/37月以上
     */
    @NotBlank(message = "年龄段不能为空")
    private String ageRange;

    /**
     * 保教费
     */
    @NotNull(message = "保教费不能为空")
    private BigDecimal educationFee;

    /**
     * 伙食费
     */
    @NotNull(message = "伙食费不能为空")
    private BigDecimal mealFee;
}
