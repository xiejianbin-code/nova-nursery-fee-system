package com.nova.backend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 幼儿查询条件DTO
 *
 * @author Nova
 */
@Data
public class ChildQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幼儿姓名
     */
    private String name;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 状态：IN_GARDEN-在园, SUSPENDED-休学, DROPPED_OUT-退学, GRADUATED-毕业
     */
    private String status;

    /**
     * 当前页码
     */
    private Long page;

    /**
     * 每页数量
     */
    private Long size;

}
