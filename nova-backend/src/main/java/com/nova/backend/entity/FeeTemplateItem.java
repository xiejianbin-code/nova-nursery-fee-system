package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收费标准模板明细实体类（年龄段收费）
 *
 * @author Nova
 */
@Data
@TableName("t_fee_template_item")
public class FeeTemplateItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 年龄段：4-12月/13-18月/19-24月/25-36月/37月以上
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
     * 删除标记：0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
