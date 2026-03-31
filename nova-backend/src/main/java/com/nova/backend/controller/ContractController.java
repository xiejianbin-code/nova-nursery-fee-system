package com.nova.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.dto.ContractChangeDTO;
import com.nova.backend.dto.ContractDTO;
import com.nova.backend.dto.ContractQueryDTO;
import com.nova.backend.dto.ContractRenewDTO;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.ContractService;
import com.nova.backend.vo.ContractVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "合同管理")
@RestController
@RequestMapping("/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @Operation(summary = "分页查询合同列表")
    @GetMapping("/page")
    public Result<Page<ContractVO>> pageList(ContractQueryDTO queryDTO) {
        Page<ContractVO> page = contractService.pageList(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取合同选项列表（不分页）")
    @GetMapping("/list")
    public Result<List<ContractVO>> listAll() {
        List<ContractVO> list = contractService.listAll();
        return Result.success(list);
    }

    @Operation(summary = "获取合同详情")
    @GetMapping("/{id}")
    public Result<ContractVO> getDetailById(
            @Parameter(description = "合同ID") @PathVariable Long id) {
        ContractVO vo = contractService.getDetailById(id);
        return Result.success(vo);
    }

    @Operation(summary = "新签合同")
    @PostMapping
    @OperLog(module = "合同管理", operation = "新签合同", type = OperationTypeEnum.INSERT)
    public Result<Boolean> create(@Valid @RequestBody ContractDTO dto) {
        boolean result = contractService.create(dto);
        return Result.success(result);
    }

    @Operation(summary = "合同变更")
    @PutMapping("/change")
    @OperLog(module = "合同管理", operation = "合同变更", type = OperationTypeEnum.UPDATE)
    public Result<Boolean> changeContract(@Valid @RequestBody ContractChangeDTO dto) {
        boolean result = contractService.changeContract(dto);
        return Result.success(result);
    }

    @Operation(summary = "终止合同")
    @PutMapping("/terminate")
    @OperLog(module = "合同管理", operation = "终止合同", type = OperationTypeEnum.DELETE)
    public Result<Boolean> terminate(
            @Parameter(description = "合同ID") @RequestParam Long id,
            @Parameter(description = "终止原因") @RequestParam String reason) {
        boolean result = contractService.terminate(id, reason);
        return Result.success(result);
    }

    @Operation(summary = "续签合同")
    @PutMapping("/renew")
    @OperLog(module = "合同管理", operation = "续签合同", type = OperationTypeEnum.UPDATE)
    public Result<Boolean> renew(@Valid @RequestBody ContractRenewDTO dto) {
        boolean result = contractService.renew(dto);
        return Result.success(result);
    }

    @Operation(summary = "获取幼儿当前生效合同")
    @GetMapping("/child/{childId}")
    public Result<ContractVO> getActiveContractByChildId(
            @Parameter(description = "幼儿ID") @PathVariable Long childId) {
        ContractVO vo = contractService.getActiveContractByChildId(childId);
        return Result.success(vo);
    }

    @Operation(summary = "根据月龄获取收费标准")
    @GetMapping("/fee")
    public Result<BigDecimal> getFeeByAge(
            @Parameter(description = "合同ID") @RequestParam Long contractId,
            @Parameter(description = "幼儿月龄") @RequestParam Integer ageMonths) {
        BigDecimal fee = contractService.getFeeByAge(contractId, ageMonths);
        return Result.success(fee);
    }
}
