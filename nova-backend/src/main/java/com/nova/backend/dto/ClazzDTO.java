package com.nova.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 班级数据传输对象
 *
 * @author Nova
 */
@Data
public class ClazzDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（更新时需要）
     */
    private Long id;

    /**
     * 班级名称
     */
    @NotBlank(message = "班级名称不能为空")
    private String className;

    /**
     * 教师名称
     */
    private String teacherName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;

}
