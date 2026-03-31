package com.nova.backend.dto;

import lombok.Data;

/**
 * 用户查询DTO
 *
 * @author Nova
 */
@Data
public class UserQueryDTO {
    private Integer page;
    private Integer limit;
    private String username;
    private String realName;
    private Integer status;
}