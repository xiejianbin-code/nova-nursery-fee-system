package com.nova.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.dto.PaymentDTO;
import com.nova.backend.dto.PaymentQueryDTO;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.PaymentService;
import com.nova.backend.vo.PaymentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收费管理")
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "分页查询收费记录")
    @GetMapping("/page")
    public Result<Page<PaymentVO>> pageList(PaymentQueryDTO queryDTO) {
        Page<PaymentVO> page = paymentService.pageList(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "收费登记")
    @PostMapping
    @OperLog(module = "收费管理", operation = "收费登记", type = OperationTypeEnum.INSERT)
    public Result<Boolean> register(@Valid @RequestBody PaymentDTO dto) {
        boolean result = paymentService.register(dto);
        return Result.success(result);
    }

    @Operation(summary = "作废收费记录")
    @PutMapping("/void")
    @OperLog(module = "收费管理", operation = "作废收费记录", type = OperationTypeEnum.DELETE)
    public Result<Boolean> voidPayment(
            @Parameter(description = "收费记录ID") @RequestParam Long id,
            @Parameter(description = "作废原因") @RequestParam String reason) {
        boolean result = paymentService.voidPayment(id, reason);
        return Result.success(result);
    }

    @Operation(summary = "查询幼儿收费记录")
    @GetMapping("/child/{childId}")
    public Result<List<PaymentVO>> getByChildId(
            @Parameter(description = "幼儿ID") @PathVariable Long childId) {
        List<PaymentVO> list = paymentService.getByChildId(childId);
        return Result.success(list);
    }
}
