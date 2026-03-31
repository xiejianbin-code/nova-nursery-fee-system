package com.nova.backend.controller;

import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.dto.UserQueryDTO;
import com.nova.backend.entity.User;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<?> pageList(UserQueryDTO queryDTO) {
        return Result.success(userService.pageList(queryDTO));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(userService.getUserDetail(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @OperLog(module = "用户管理", operation = "新增用户", type = OperationTypeEnum.INSERT)
    public Result<?> add(@RequestBody User user) {
        userService.addUser(user);
        return Result.success();
    }

    @Operation(summary = "修改用户")
    @PutMapping
    @OperLog(module = "用户管理", operation = "修改用户", type = OperationTypeEnum.UPDATE)
    public Result<?> update(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @OperLog(module = "用户管理", operation = "删除用户", type = OperationTypeEnum.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}/status")
    @OperLog(module = "用户管理", operation = "更新用户状态", type = OperationTypeEnum.UPDATE)
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        userService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取当前用户权限")
    @GetMapping("/permissions")
    public Result<?> getUserPermissions() {
        return Result.success(userService.getCurrentUserPermissions());
    }

    @Operation(summary = "修改密码")
    @PostMapping("/changePassword")
    @OperLog(module = "用户管理", operation = "修改密码", type = OperationTypeEnum.UPDATE, saveRequest = false)
    public Result<?> changePassword(@RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        userService.changePassword(oldPassword, newPassword);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/{id}/resetPassword")
    @OperLog(module = "用户管理", operation = "重置密码", type = OperationTypeEnum.UPDATE)
    public Result<?> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    @Operation(summary = "获取老师列表")
    @GetMapping("/teachers")
    public Result<?> getTeacherList() {
        return Result.success(userService.getTeacherList());
    }

    @Operation(summary = "更新当前用户信息")
    @PutMapping("/current")
    @OperLog(module = "用户管理", operation = "更新个人信息", type = OperationTypeEnum.UPDATE)
    public Result<?> updateCurrentUserInfo(@RequestBody User user) {
        userService.updateCurrentUserInfo(user);
        return Result.success();
    }
}
