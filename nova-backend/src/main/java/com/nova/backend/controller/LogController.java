package com.nova.backend.controller;

import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "日志管理", description = "日志管理相关接口")
@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    public Result<?> pageList(
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String operationStatus
    ) {
        return Result.success(logService.pageList(page, limit, username, module, startTime, endTime, operationType, operationStatus));
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{ids}")
    @OperLog(module = "日志管理", operation = "删除日志", type = OperationTypeEnum.DELETE)
    public Result<?> deleteByIds(@PathVariable Long[] ids) {
        logService.deleteByIds(ids);
        return Result.success();
    }

    @Operation(summary = "清理日志")
    @DeleteMapping("/clear")
    @OperLog(module = "日志管理", operation = "清理日志", type = OperationTypeEnum.DELETE)
    public Result<?> clearLogs(@RequestParam(defaultValue = "30") Integer days) {
        logService.clearLogs(days);
        return Result.success();
    }
}
