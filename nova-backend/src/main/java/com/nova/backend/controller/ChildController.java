package com.nova.backend.controller;

import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.entity.Child;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.ChildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "幼儿管理", description = "幼儿管理相关接口")
@RestController
@RequestMapping("/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @Operation(summary = "分页查询幼儿列表")
    @GetMapping("/page")
    public Result<?> pageList(
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(childService.pageList(page, limit, name, classId, status));
    }

    @Operation(summary = "获取幼儿列表（不分页）")
    @GetMapping("/list")
    public Result<List<Child>> list(
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(childService.getChildList(status));
    }

    @Operation(summary = "获取幼儿详情")
    @GetMapping("/{id}")
    public Result<Child> getById(@PathVariable Long id) {
        return Result.success(childService.getById(id));
    }

    @Operation(summary = "新增幼儿")
    @PostMapping
    @OperLog(module = "幼儿管理", operation = "新增幼儿", type = OperationTypeEnum.INSERT)
    public Result<?> add(@RequestBody Child child) {
        childService.save(child);
        return Result.success();
    }

    @Operation(summary = "修改幼儿")
    @PutMapping
    @OperLog(module = "幼儿管理", operation = "修改幼儿", type = OperationTypeEnum.UPDATE)
    public Result<?> update(@RequestBody Child child) {
        childService.updateById(child);
        return Result.success();
    }

    @Operation(summary = "删除幼儿")
    @DeleteMapping("/{id}")
    @OperLog(module = "幼儿管理", operation = "删除幼儿", type = OperationTypeEnum.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        childService.removeById(id);
        return Result.success();
    }
}
