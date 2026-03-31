package com.nova.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.common.Result;
import com.nova.backend.dto.BalanceRecordQueryDTO;
import com.nova.backend.service.BalanceService;
import com.nova.backend.vo.BalanceRecordVO;
import com.nova.backend.vo.BalanceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 余额管理控制器
 *
 * @author Nova
 */
@Tag(name = "余额管理")
@RestController
@RequestMapping("/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    /**
     * 查询幼儿三账户余额
     */
    @Operation(summary = "查询幼儿三账户余额")
    @GetMapping("/{childId}")
    public Result<BalanceVO> getAllBalances(
            @Parameter(description = "幼儿ID") @PathVariable Long childId) {
        BalanceVO vo = balanceService.getAllBalances(childId);
        return Result.success(vo);
    }

    /**
     * 查询余额变动记录
     */
    @Operation(summary = "查询余额变动记录")
    @GetMapping("/record")
    public Result<Page<BalanceRecordVO>> getBalanceRecords(BalanceRecordQueryDTO queryDTO) {
        Page<BalanceRecordVO> page = balanceService.getBalanceRecords(queryDTO);
        return Result.success(page);
    }
}
