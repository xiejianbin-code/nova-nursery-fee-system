package com.nova.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 出勤登记数据传输对象
 *
 * @author Nova
 */
@Data
public class AttendanceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（修改时需要）
     */
    private Long id;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 出勤日期
     */
    private LocalDate attendanceDate;

    /**
     * 出勤状态：1-正常到园，2-请假，3-病假，4-缺勤
     */
    @NotNull(message = "出勤状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}
