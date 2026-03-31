package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 出勤记录实体类
 *
 * @author Nova
 */
@Data
@TableName("t_attendance")
public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private Long childId;

    private LocalDate attendanceDate;

    private Integer status;

    private Long registerUser;

    private Long modifyUser;

    private String remark;

    private LocalDateTime createTime;

    private Long createUser;

    private LocalDateTime updateTime;

    private Long updateUser;

    @TableLogic
    private Integer deleted;
}