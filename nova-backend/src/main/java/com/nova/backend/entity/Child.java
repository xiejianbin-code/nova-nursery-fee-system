package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 幼儿实体类
 *
 * @author Nova
 */
@Data
@TableName("t_child")
public class Child implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String name;

    private Integer gender;

    private LocalDate birthDate;

    private Long classId;

    private LocalDate enrollDate;

    private String parentName;

    private String contactPhone;

    private String wechat;

    private String emergencyContact;

    private String emergencyPhone;

    private String pickupPerson;

    private String allergyHistory;

    private Integer status;

    private LocalDateTime createTime;

    private Long createUser;

    private LocalDateTime updateTime;

    private Long updateUser;

    @TableLogic
    private Integer deleted;

}