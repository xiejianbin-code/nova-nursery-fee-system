package com.nova.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.dto.FeeTemplateDTO;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.FeeTemplateService;
import com.nova.backend.vo.FeeTemplateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收费标准模板管理")
@RestController
@RequestMapping("/fee-template")
@RequiredArgsConstructor
public class FeeTemplateController {

    private final FeeTemplateService feeTemplateService;

    @Operation(summary = "分页查询模板列表")
    @GetMapping("/page")
    public Result<Page<FeeTemplateVO>> pageList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "模板名称") @RequestParam(required = false) String templateName,
            @Parameter(description = "课程类型") @RequestParam(required = false) String courseType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Page<FeeTemplateVO> page = feeTemplateService.pageList(pageNum, pageSize, templateName, courseType, status);
        return Result.success(page);
    }

    @Operation(summary = "获取模板详情")
    @GetMapping("/{id}")
    public Result<FeeTemplateVO> getDetailById(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        FeeTemplateVO vo = feeTemplateService.getDetailById(id);
        return Result.success(vo);
    }

    @Operation(summary = "新增模板")
    @PostMapping
    @OperLog(module = "收费标准管理", operation = "新增收费标准", type = OperationTypeEnum.INSERT)
    public Result<Boolean> save(@Valid @RequestBody FeeTemplateDTO dto) {
        boolean result = feeTemplateService.save(dto);
        return Result.success(result);
    }

    @Operation(summary = "更新模板")
    @PutMapping
    @OperLog(module = "收费标准管理", operation = "更新收费标准", type = OperationTypeEnum.UPDATE)
    public Result<Boolean> updateById(@Valid @RequestBody FeeTemplateDTO dto) {
        boolean result = feeTemplateService.updateById(dto);
        return Result.success(result);
    }

    @Operation(summary = "更新模板状态")
    @PutMapping("/status")
    @OperLog(module = "收费标准管理", operation = "更新收费标准状态", type = OperationTypeEnum.UPDATE)
    public Result<Boolean> updateStatus(
            @Parameter(description = "模板ID") @RequestParam Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        boolean result = feeTemplateService.updateStatus(id, status);
        return Result.success(result);
    }

    @Operation(summary = "获取启用模板列表")
    @GetMapping("/enabled")
    public Result<List<FeeTemplateVO>> listEnabled() {
        List<FeeTemplateVO> list = feeTemplateService.listEnabled();
        return Result.success(list);
    }
}
