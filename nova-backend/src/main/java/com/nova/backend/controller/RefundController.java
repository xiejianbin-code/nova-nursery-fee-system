package com.nova.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.dto.RefundDTO;
import com.nova.backend.dto.RefundQueryDTO;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.RefundService;
import com.nova.backend.vo.RefundVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "退费管理")
@RestController
@RequestMapping("/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @Operation(summary = "分页查询退费记录")
    @GetMapping("/page")
    public Result<Page<RefundVO>> pageList(RefundQueryDTO queryDTO) {
        Page<RefundVO> page = refundService.pageList(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "退费登记")
    @PostMapping
    @OperLog(module = "退费管理", operation = "退费登记", type = OperationTypeEnum.INSERT)
    public Result<Boolean> register(@Valid @RequestBody RefundDTO dto) {
        boolean result = refundService.register(dto);
        return Result.success(result);
    }

    @Operation(summary = "查询幼儿退费记录")
    @GetMapping("/child/{childId}")
    public Result<List<RefundVO>> getByChildId(
            @Parameter(description = "幼儿ID") @PathVariable Long childId) {
        List<RefundVO> list = refundService.getByChildId(childId);
        return Result.success(list);
    }
}
