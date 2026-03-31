package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单详情响应对象
 *
 * @author Nova
 */
@Data
public class BillVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 账单编号
     */
    private String billNo;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 幼儿姓名
     */
    private String childName;

    /**
     * 幼儿性别
     */
    private String childGender;

    /**
     * 幼儿出生日期
     */
    private LocalDate childBirthDate;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 合同ID
     */
    private String contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 账单月份
     */
    private String billMonth;

    /**
     * 保教费应收
     */
    private BigDecimal educationFeeReceivable;

    /**
     * 伙食费应收
     */
    private BigDecimal mealFeeReceivable;

    /**
     * 其他费用应收
     */
    private BigDecimal otherFeeReceivable;

    /**
     * 保教费应退
     */
    private BigDecimal educationFeeRefund;

    /**
     * 伙食费应退
     */
    private BigDecimal mealFeeRefund;

    /**
     * 减免金额
     */
    private BigDecimal discountAmount;

    /**
     * 实际应交金额
     */
    private BigDecimal actualAmount;

    /**
     * 实际需缴金额
     */
    private BigDecimal dueAmount;

    /**
     * 账单状态
     */
    private String status;

    /**
     * 账单状态描述
     */
    private String statusDesc;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 确认人姓名
     */
    private String confirmUserName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 账单明细列表
     */
    private List<BillItemVO> items;

    /**
     * 账单明细响应对象
     */
    @Data
    public static class BillItemVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 主键ID
         */
        private Long id;

        /**
         * 明细类型
         */
        private String itemType;

        /**
         * 明细类型描述
         */
        private String itemTypeDesc;

        /**
         * 费用项目名称
         */
        private String feeItem;

        /**
         * 金额
         */
        private BigDecimal amount;

        /**
         * 说明
         */
        private String description;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

    }

}
