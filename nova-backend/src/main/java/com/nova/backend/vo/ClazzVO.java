package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 班级视图对象
 *
 * @author Nova
 */
@Data
public class ClazzVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String className;

    private Long teacherId;

    private String teacherName;

    private String teacherPhone;

    private Integer currentCount;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;
}
