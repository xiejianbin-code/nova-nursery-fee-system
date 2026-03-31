package com.nova.backend.controller;

import com.nova.backend.common.Result;
import com.nova.backend.entity.Role;
import com.nova.backend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author Nova
 */
@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    public Result<?> pageList(
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(roleService.pageList(page, limit, name, status));
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping("/list")
    public Result<?> getList() {
        return Result.success(roleService.getAllRoles());
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(roleService.getRoleDetail(id));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public Result<?> add(@RequestBody Role role) {
        roleService.addRole(role);
        return Result.success();
    }

    @Operation(summary = "修改角色")
    @PutMapping
    public Result<?> update(@RequestBody Role role) {
        roleService.updateRole(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @Operation(summary = "获取角色菜单权限")
    @GetMapping("/{id}/menus")
    public Result<?> getRoleMenus(@PathVariable Long id) {
        return Result.success(roleService.getRoleMenus(id));
    }

    @Operation(summary = "设置角色菜单权限")
    @PostMapping("/{id}/menus")
    public Result<?> setRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.setRoleMenus(id, menuIds);
        return Result.success();
    }
}