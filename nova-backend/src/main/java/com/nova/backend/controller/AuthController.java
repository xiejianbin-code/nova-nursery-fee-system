package com.nova.backend.controller;

import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.dto.CurrentUserVO;
import com.nova.backend.dto.LoginDTO;
import com.nova.backend.dto.LoginVO;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证管理", description = "认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户登录", description = "用户登录接口")
    @PostMapping("/login")
    @OperLog(module = "认证管理", operation = "用户登录", type = OperationTypeEnum.LOGIN, saveResponse = false)
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    @Operation(summary = "用户登出", description = "用户登出接口")
    @PostMapping("/logout")
    @OperLog(module = "认证管理", operation = "用户登出", type = OperationTypeEnum.LOGOUT)
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户信息接口")
    @GetMapping("/info")
    public Result<CurrentUserVO> getCurrentUserInfo() {
        CurrentUserVO currentUserVO = userService.getCurrentUserInfo();
        return Result.success(currentUserVO);
    }
}
