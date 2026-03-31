package com.nova.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 合同其他费用数据传输对象
 *
 * @author Nova
 */
@Data
public class ContractOtherFeeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（更新时使用）
     */
    private Long id;

    /**
     * 费用编码
     */
    @NotBlank(message = "费用编码不能为空")
    private String feeCode;

    /**
     * 费用名称
     */
    @NotBlank(message = "费用名称不能为空")
    private String feeName;

    /**
     * 收费周期：ONCE-一次性, MONTHLY-按月, SEMESTER-按学期
     */
    @NotBlank(message = "收费周期不能为空")
    private String chargeCycle;

    /**
     * 收费标准
     */
    @NotNull(message = "收费标准不能为空")
    private BigDecimal feeStandard;

    /**
     * 状态：0-停用, 1-启用
     */
    private Integer status;
}
