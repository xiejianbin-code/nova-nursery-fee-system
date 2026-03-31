package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 班级查询条件DTO
 *
 * @author Nova
 */
@Data
public class ClazzQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 教师名称
     */
    private String teacherName;

    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;

    /**
     * 当前页码
     */
    private Long page;

    /**
     * 每页数量
     */
    private Long size;

}
