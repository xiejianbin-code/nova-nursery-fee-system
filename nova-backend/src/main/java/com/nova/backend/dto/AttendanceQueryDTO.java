package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 出勤查询条件数据传输对象
 *
 * @author Nova
 */
@Data
public class AttendanceQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

}
