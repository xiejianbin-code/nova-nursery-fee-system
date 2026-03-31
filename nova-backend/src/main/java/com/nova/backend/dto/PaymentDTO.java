package com.nova.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 收费登记数据传输对象
 *
 * @author Nova
 */
@Data
public class PaymentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幼儿ID
     */
    @NotNull(message = "幼儿ID不能为空")
    private Long childId;

    /**
     * 费用类型：EDUCATION-保教费, MEAL-伙食费, OTHER-其他费用
     */
    @NotBlank(message = "费用类型不能为空")
    private String paymentType;

    /**
     * 费用项目名称
     */
    @NotBlank(message = "费用项目不能为空")
    private String feeItem;

    /**
     * 收费金额
     */
    @NotNull(message = "收费金额不能为空")
    private BigDecimal amount;

    /**
     * 支付方式：WECHAT-微信, ALIPAY-支付宝, CASH-现金, TRANSFER-转账
     */
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    /**
     * 备注
     */
    private String remark;
}
