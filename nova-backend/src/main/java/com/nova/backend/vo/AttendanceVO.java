package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 出勤记录响应对象
 *
 * @author Nova
 */
@Data
public class AttendanceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

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
     * 出勤日期
     */
    private LocalDate attendanceDate;

    /**
     * 出勤状态：1-正常到园, 2-请假, 3-病假, 4-缺勤
     */
    private Integer status;

    /**
     * 出勤状态描述
     */
    private String statusDesc;

    /**
     * 登记人姓名
     */
    private String registerUserName;

    /**
     * 登记时间
     */
    private LocalDateTime registerTime;

    /**
     * 修改人姓名
     */
    private String modifyUserName;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

}
