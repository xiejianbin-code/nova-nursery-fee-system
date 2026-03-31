package com.nova.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.dto.BillBatchGenerateDTO;
import com.nova.backend.dto.BillDiscountDTO;
import com.nova.backend.dto.BillGenerateDTO;
import com.nova.backend.dto.BillOtherFeeDTO;
import com.nova.backend.dto.BillQueryDTO;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.BillService;
import com.nova.backend.vo.BillVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Tag(name = "账单管理")
@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @Operation(summary = "分页查询账单列表")
    @GetMapping("/page")
    public Result<Page<BillVO>> pageList(BillQueryDTO queryDTO) {
        Page<BillVO> page = billService.pageList(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取账单详情")
    @GetMapping("/{id}")
    public Result<BillVO> getDetailById(
            @Parameter(description = "账单ID") @PathVariable Long id) {
        BillVO vo = billService.getDetailById(id);
        return Result.success(vo);
    }

    @Operation(summary = "获取账单明细列表")
    @GetMapping("/{id}/items")
    public Result<List<BillVO.BillItemVO>> getBillItems(
            @Parameter(description = "账单ID") @PathVariable Long id) {
        List<BillVO.BillItemVO> items = billService.getBillItems(id);
        return Result.success(items);
    }

    @Operation(summary = "手动生成账单")
    @PostMapping("/generate")
    @OperLog(module = "账单管理", operation = "手动生成账单", type = OperationTypeEnum.INSERT)
    public Result<Boolean> generateBill(@Valid @RequestBody BillGenerateDTO dto) {
        boolean result = billService.generateBill(dto, null);
        return Result.success(result);
    }

    @Operation(summary = "批量生成账单")
    @PostMapping("/batch-generate")
    @OperLog(module = "账单管理", operation = "批量生成账单", type = OperationTypeEnum.INSERT)
    public Result<Integer> batchGenerateBill(@Valid @RequestBody BillBatchGenerateDTO dto) {
        int count = billService.batchGenerateBill(dto);
        return Result.success("成功生成" + count + "条账单", count);
    }

    @Operation(summary = "生成上月账单")
    @PostMapping("/generate-monthly")
    @OperLog(module = "账单管理", operation = "生成上月账单", type = OperationTypeEnum.INSERT)
    public Result<Integer> generateMonthlyBills() {
        int count = billService.generateMonthlyBills();
        return Result.success("成功生成" + count + "条账单", count);
    }

    @Operation(summary = "确认账单")
    @PutMapping("/confirm")
    @OperLog(module = "账单管理", operation = "确认账单", type = OperationTypeEnum.UPDATE)
    public Result<Boolean> confirmBill(
            @Parameter(description = "账单ID") @RequestParam Long id) {
        boolean result = billService.confirmBill(id, null);
        return Result.success(result);
    }

    @Operation(summary = "重新核算账单")
    @PutMapping("/recalculate")
    @OperLog(module = "账单管理", operation = "重新核算账单", type = OperationTypeEnum.UPDATE)
    public Result<Boolean> recalculateBill(
            @Parameter(description = "账单ID") @RequestParam Long id) {
        boolean result = billService.recalculateBill(id, null);
        return Result.success(result);
    }

    @Operation(summary = "账单减免")
    @PutMapping("/discount")
    @OperLog(module = "账单管理", operation = "账单减免", type = OperationTypeEnum.UPDATE)
    public Result<Boolean> discountBill(@Valid @RequestBody BillDiscountDTO dto) {
        boolean result = billService.discountBill(dto, null);
        return Result.success(result);
    }

    @Operation(summary = "添加其他费用")
    @PostMapping("/other-fee")
    @OperLog(module = "账单管理", operation = "添加其他费用", type = OperationTypeEnum.INSERT)
    public Result<Boolean> addOtherFee(@Valid @RequestBody BillOtherFeeDTO dto) {
        boolean result = billService.addOtherFee(dto, null);
        return Result.success(result);
    }

    @Operation(summary = "导出账单")
    @GetMapping("/export")
    @OperLog(module = "账单管理", operation = "导出账单", type = OperationTypeEnum.EXPORT)
    public ResponseEntity<byte[]> exportBills(
            @Parameter(description = "账单ID列表") @RequestParam List<Long> ids) {
        byte[] data = billService.exportBills(ids);

        String fileName = "账单_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
