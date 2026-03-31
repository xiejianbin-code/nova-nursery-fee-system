package com.nova.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 收费标准模板数据传输对象
 *
 * @author Nova
 */
@Data
public class FeeTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（更新时必填）
     */
    private Long id;

    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    /**
     * 课程类型：MONTHLY-按月, SEMESTER-按学期
     */
    @NotBlank(message = "课程类型不能为空")
    private String courseType;

    /**
     * 状态：0-停用, 1-启用
     */
    private Integer status;

    /**
     * 年龄段收费明细列表
     */
    @Valid
    @NotNull(message = "年龄段收费明细不能为空")
    private List<FeeTemplateItemDTO> items;

    /**
     * 其他费用列表
     */
    @Valid
    private List<OtherFeeTemplateDTO> otherFees;
}
