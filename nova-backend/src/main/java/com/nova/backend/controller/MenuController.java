package com.nova.backend.controller;

import com.nova.backend.common.Result;
import com.nova.backend.dto.UserMenuVO;
import com.nova.backend.entity.Menu;
import com.nova.backend.entity.User;
import com.nova.backend.service.MenuService;
import com.nova.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 *
 * @author Nova
 */
@Tag(name = "菜单管理", description = "菜单相关接口")
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final UserService userService;

    @Operation(summary = "获取用户菜单", description = "获取当前登录用户的菜单列表")
    @GetMapping("/user")
    public Result<List<UserMenuVO>> getUserMenus() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return Result.success(List.of());
        }
        List<UserMenuVO> menus = menuService.getUserMenus(user.getId());
        return Result.success(menus);
    }

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public Result<List<UserMenuVO>> getTree() {
        return Result.success(menuService.getMenuTree());
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public Result<Menu> getById(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public Result<?> create(@RequestBody Menu menu) {
        menuService.save(menu);
        return Result.success();
    }

    @Operation(summary = "更新菜单")
    @PutMapping
    public Result<?> update(@RequestBody Menu menu) {
        menuService.updateById(menu);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<?> deleteById(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }
}