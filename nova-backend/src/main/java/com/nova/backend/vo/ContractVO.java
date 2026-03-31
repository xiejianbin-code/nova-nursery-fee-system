package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 合同详情响应对象
 *
 * @author Nova
 */
@Data
public class ContractVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 幼儿姓名
     */
    private String childName;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 课程类型：MONTHLY-按月, SEMESTER-按学期
     */
    private String courseType;

    /**
     * 合同开始日期
     */
    private LocalDate startDate;

    /**
     * 合同结束日期
     */
    private LocalDate endDate;

    /**
     * 合同状态：1-生效中, 2-已到期, 3-已变更, 4-已终止
     */
    private Integer status;

    /**
     * 附件URL
     */
    private String attachmentUrl;

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
     * 年龄段收费明细列表
     */
    private List<ContractFeeItemVO> feeItems;

    /**
     * 其他费用列表
     */
    private List<ContractOtherFeeVO> otherFees;

    /**
     * 变更历史列表
     */
    private List<ContractChangeLogVO> changeLogs;

    /**
     * 年龄段收费明细响应对象
     */
    @Data
    public static class ContractFeeItemVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 主键ID
         */
        private Long id;

        /**
         * 年龄段
         */
        private String ageRange;

        /**
         * 保教费
         */
        private BigDecimal educationFee;

        /**
         * 伙食费
         */
        private BigDecimal mealFee;
    }

    /**
     * 其他费用响应对象
     */
    @Data
    public static class ContractOtherFeeVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 主键ID
         */
        private Long id;

        /**
         * 费用编码
         */
        private String feeCode;

        /**
         * 费用名称
         */
        private String feeName;

        /**
         * 收费周期
         */
        private String chargeCycle;

        /**
         * 收费标准
         */
        private BigDecimal feeStandard;

        /**
         * 状态
         */
        private Integer status;
    }

    /**
     * 合同变更记录响应对象
     */
    @Data
    public static class ContractChangeLogVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 主键ID
         */
        private Long id;

        /**
         * 合同ID
         */
        private Long contractId;

        /**
         * 变更类型：CHANGE-变更, TERMINATE-终止, RENEW-续签
         */
        private String changeType;

        /**
         * 变更原因
         */
        private String changeReason;

        /**
         * 变更前内容（JSON格式）
         */
        private String beforeContent;

        /**
         * 变更后内容（JSON格式）
         */
        private String afterContent;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

        /**
         * 创建人
         */
        private Long createUser;
    }
}
