package com.nova.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户菜单VO
 *
 * @author Nova
 */
@Data
@Schema(description = "用户菜单")
public class UserMenuVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单类型：1-目录，2-菜单，3-按钮")
    private Integer menuType;

    @Schema(description = "路由路径")
    private String routePath;

    @Schema(description = "路由名称")
    private String name;

    @Schema(description = "组件路径")
    private String componentPath;

    @Schema(description = "权限标识")
    private String permission;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态：0-停用，1-启用")
    private Integer status;

    @Schema(description = "是否隐藏")
    private Integer hidden;

    @Schema(description = "重定向路径")
    private String redirect;

    @Schema(description = "子菜单")
    private List<UserMenuVO> children;
}