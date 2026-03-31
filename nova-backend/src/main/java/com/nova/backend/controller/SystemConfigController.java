package com.nova.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.common.Result;
import com.nova.backend.entity.SystemConfig;
import com.nova.backend.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 *
 * @author Nova
 */
@Tag(name = "系统配置管理", description = "系统配置管理相关接口")
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @Operation(summary = "分页查询系统配置列表")
    @GetMapping("/page")
    public Result<?> pageList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) Integer status) {
        
        Page<SystemConfig> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        
        if (configKey != null && !configKey.isEmpty()) {
            queryWrapper.like(SystemConfig::getConfigKey, configKey);
        }
        if (status != null) {
            queryWrapper.eq(SystemConfig::getStatus, status);
        }
        
        queryWrapper.orderByDesc(SystemConfig::getCreateTime);
        
        return Result.success(systemConfigService.page(page, queryWrapper));
    }

    @Operation(summary = "获取所有启用的系统配置")
    @GetMapping("/list")
    public Result<?> list() {
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfig::getStatus, 1);
        queryWrapper.orderByAsc(SystemConfig::getConfigKey);
        return Result.success(systemConfigService.list(queryWrapper));
    }

    @Operation(summary = "根据配置键获取配置值")
    @GetMapping("/value/{configKey}")
    public Result<?> getConfigValue(@PathVariable String configKey) {
        String value = systemConfigService.getConfigValue(configKey);
        return Result.success(value);
    }

    @Operation(summary = "获取系统配置详情")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(systemConfigService.getById(id));
    }

    @Operation(summary = "新增系统配置")
    @PostMapping
    public Result<?> add(@RequestBody SystemConfig systemConfig) {
        systemConfigService.save(systemConfig);
        return Result.success();
    }

    @Operation(summary = "修改系统配置")
    @PutMapping
    public Result<?> update(@RequestBody SystemConfig systemConfig) {
        systemConfigService.updateById(systemConfig);
        return Result.success();
    }

    @Operation(summary = "删除系统配置")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        systemConfigService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "批量删除系统配置")
    @DeleteMapping("/batch")
    public Result<?> batchDelete(@RequestBody List<Long> ids) {
        systemConfigService.removeByIds(ids);
        return Result.success();
    }

    @Operation(summary = "更新系统配置状态")
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setId(id);
        systemConfig.setStatus(status);
        systemConfigService.updateById(systemConfig);
        return Result.success();
    }
}
