package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 收费标准模板详情响应对象
 *
 * @author Nova
 */
@Data
public class FeeTemplateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 课程类型：MONTHLY-按月, SEMESTER-按学期
     */
    private String courseType;

    /**
     * 状态：0-停用, 1-启用
     */
    private Integer status;

    /**
     * 版本号
     */
    private Integer version;

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
    private List<FeeTemplateItemVO> items;

    /**
     * 其他费用列表
     */
    private List<OtherFeeTemplateVO> otherFees;

    /**
     * 年龄段收费明细响应对象
     */
    @Data
    public static class FeeTemplateItemVO implements Serializable {

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
    public static class OtherFeeTemplateVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 主键ID
         */
        private Long id;

        /**
         * 费用代码
         */
        private String feeCode;

        /**
         * 费用名称
         */
        private String feeName;

        /**
         * 收取周期
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
}
