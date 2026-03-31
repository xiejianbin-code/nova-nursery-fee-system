package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 出勤统计响应对象
 *
 * @author Nova
 */
@Data
public class AttendanceStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 幼儿姓名
     */
    private String childName;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 应出勤天数
     */
    private Integer totalDays;

    /**
     * 正常到园天数
     */
    private Integer normalDays;

    /**
     * 请假天数
     */
    private Integer leaveDays;

    /**
     * 病假天数
     */
    private Integer sickDays;

    /**
     * 缺勤天数
     */
    private Integer absentDays;

    /**
     * 出勤率（百分比）
     */
    private Double attendanceRate;

}
