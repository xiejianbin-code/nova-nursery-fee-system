package com.nova.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 当前用户信息响应VO
 * 包含用户信息、角色和权限
 *
 * @author Nova
 */
@Data
@Schema(description = "当前用户信息响应")
public class CurrentUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户信息")
    private UserInfoVO user;

    @Schema(description = "角色列表")
    private List<String> roles;

    @Schema(description = "角色名称列表")
    private List<String> roleNames;

    @Schema(description = "权限列表")
    private List<String> permissions;
}
