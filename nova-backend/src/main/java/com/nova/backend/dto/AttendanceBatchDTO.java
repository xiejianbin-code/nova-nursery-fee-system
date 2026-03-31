package com.nova.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 批量出勤登记数据传输对象
 *
 * @author Nova
 */
@Data
public class AttendanceBatchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 班级ID
     */
    @NotNull(message = "班级ID不能为空")
    private Long classId;

    /**
     * 出勤日期
     */
    @NotNull(message = "出勤日期不能为空")
    private LocalDate attendanceDate;

    /**
     * 正常到园的幼儿ID列表
     */
    private List<Long> childIds;

    /**
     * 请假的幼儿ID列表
     */
    private List<Long> leaveChildIds;

    /**
     * 病假的幼儿ID列表
     */
    private List<Long> sickChildIds;

    /**
     * 缺勤的幼儿ID列表
     */
    private List<Long> absentChildIds;

}
