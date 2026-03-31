package com.nova.backend.controller;

import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.entity.Clazz;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.ClazzService;
import com.nova.backend.vo.ClazzVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "班级管理", description = "班级管理相关接口")
@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClazzController {

    private final ClazzService clazzService;

    @Operation(summary = "分页查询班级列表")
    @GetMapping("/page")
    public Result<?> pageList(
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(clazzService.pageList(page, limit, className, status));
    }

    @Operation(summary = "获取班级选项列表")
    @GetMapping("/options")
    public Result<List<Clazz>> getOptions() {
        return Result.success(clazzService.getOptions());
    }

    @Operation(summary = "获取班级详情")
    @GetMapping("/{id}")
    public Result<ClazzVO> getById(@PathVariable Long id) {
        return Result.success(clazzService.getClazzDetail(id));
    }

    @Operation(summary = "新增班级")
    @PostMapping
    @OperLog(module = "班级管理", operation = "新增班级", type = OperationTypeEnum.INSERT)
    public Result<?> add(@RequestBody Clazz clazz) {
        clazzService.save(clazz);
        return Result.success();
    }

    @Operation(summary = "修改班级")
    @PutMapping
    @OperLog(module = "班级管理", operation = "修改班级", type = OperationTypeEnum.UPDATE)
    public Result<?> update(@RequestBody Clazz clazz) {
        clazzService.updateById(clazz);
        return Result.success();
    }

    @Operation(summary = "删除班级")
    @DeleteMapping("/{id}")
    @OperLog(module = "班级管理", operation = "删除班级", type = OperationTypeEnum.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        clazzService.removeById(id);
        return Result.success();
    }
}
