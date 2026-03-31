package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 收费查询条件DTO
 *
 * @author Nova
 */
@Data
public class PaymentQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收据编号
     */
    private String receiptNo;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 幼儿姓名
     */
    private String childName;

    /**
     * 费用类型：EDUCATION-保教费, MEAL-伙食费, OTHER-其他费用
     */
    private String paymentType;

    /**
     * 支付方式：WECHAT-微信, ALIPAY-支付宝, CASH-现金, TRANSFER-转账
     */
    private String paymentMethod;

    /**
     * 状态：VALID-有效, VOIDED-已作废
     */
    private String status;

    /**
     * 收费日期-起始
     */
    private LocalDate paymentDateBegin;

    /**
     * 收费日期-结束
     */
    private LocalDate paymentDateEnd;

    /**
     * 当前页码
     */
    private Long page;

    /**
     * 每页数量
     */
    private Long size;
}
