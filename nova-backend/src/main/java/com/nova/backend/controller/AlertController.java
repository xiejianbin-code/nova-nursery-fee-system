package com.nova.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.common.Result;
import com.nova.backend.dto.AlertQueryDTO;
import com.nova.backend.enums.AlertTypeEnum;
import com.nova.backend.service.AlertService;
import com.nova.backend.vo.AlertVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预警管理控制器
 *
 * @author Nova
 */
@Tag(name = "预警管理")
@RestController
@RequestMapping("/alert")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    /**
     * 分页查询预警列表
     */
    @Operation(summary = "分页查询预警列表")
    @GetMapping("/page")
    public Result<Page<AlertVO>> pageList(AlertQueryDTO queryDTO) {
        Page<AlertVO> page = alertService.pageList(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取未处理预警数量
     */
    @Operation(summary = "获取未处理预警数量")
    @GetMapping("/unhandle-count")
    public Result<Long> getUnhandleCount() {
        Long count = alertService.getUnhandleCount();
        return Result.success(count);
    }

    /**
     * 处理预警
     */
    @Operation(summary = "处理预警")
    @PutMapping("/handle")
    public Result<Boolean> handle(
            @Parameter(description = "预警ID") @RequestParam Long id,
            @Parameter(description = "处理备注") @RequestParam(required = false) String remark) {
        boolean result = alertService.handle(id, remark);
        return Result.success(result);
    }

    /**
     * 获取所有预警类型
     */
    @Operation(summary = "获取所有预警类型")
    @GetMapping("/types")
    public Result<List<Map<String, Object>>> getAlertTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        
        for (AlertTypeEnum alertType : AlertTypeEnum.values()) {
            Map<String, Object> typeMap = new HashMap<>();
            typeMap.put("value", alertType.getCode());
            typeMap.put("label", alertType.getName());
            types.add(typeMap);
        }
        
        return Result.success(types);
    }
}