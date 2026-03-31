package com.nova.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 退费登记数据传输对象
 *
 * @author Nova
 */
@Data
public class RefundDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幼儿ID
     */
    @NotNull(message = "幼儿ID不能为空")
    private Long childId;

    /**
     * 账户类型：EDUCATION-保教费, MEAL-伙食费, OTHER-其他费用
     */
    @NotBlank(message = "账户类型不能为空")
    private String accountType;

    /**
     * 费用项目名称
     */
    @NotBlank(message = "费用项目不能为空")
    private String feeItem;

    /**
     * 退费金额
     */
    @NotNull(message = "退费金额不能为空")
    private BigDecimal amount;

    /**
     * 退费原因
     */
    @NotBlank(message = "退费原因不能为空")
    private String reason;
}
