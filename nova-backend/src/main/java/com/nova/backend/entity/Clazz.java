package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 班级实体类
 *
 * @author Nova
 */
@Data
@TableName("t_class")
public class Clazz implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String className;

    private Long teacherId;

    private String remark;

    private Integer status;

    private Integer currentCount;

    private LocalDateTime createTime;

    private Long createUser;

    private LocalDateTime updateTime;

    private Long updateUser;

    @TableLogic
    private Integer deleted;
}