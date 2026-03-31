package com.nova.backend.controller;

import com.nova.backend.annotation.OperLog;
import com.nova.backend.common.Result;
import com.nova.backend.dto.AttendanceBatchDTO;
import com.nova.backend.dto.AttendanceDTO;
import com.nova.backend.dto.AttendanceQueryDTO;
import com.nova.backend.entity.User;
import com.nova.backend.enums.OperationTypeEnum;
import com.nova.backend.service.AttendanceService;
import com.nova.backend.service.UserService;
import com.nova.backend.vo.AttendanceStatisticsVO;
import com.nova.backend.vo.AttendanceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "出勤管理", description = "出勤管理相关接口")
@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserService userService;

    @Operation(summary = "分页查询出勤记录", description = "分页查询出勤记录列表")
    @GetMapping("/page")
    public Result<?> pageList(
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam(required = false) Long childId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String date
    ) {
        return Result.success(attendanceService.pageList(page, limit, childId, classId, date));
    }

    @Operation(summary = "出勤登记", description = "单个幼儿出勤登记")
    @PostMapping("/register")
    @OperLog(module = "出勤管理", operation = "出勤登记", type = OperationTypeEnum.INSERT)
    public Result<Boolean> register(@Valid @RequestBody AttendanceDTO dto) {
        if (dto.getChildId() == null) {
            return Result.failed("幼儿ID不能为空");
        }
        if (dto.getAttendanceDate() == null) {
            return Result.failed("出勤日期不能为空");
        }
        Long currentUserId = getCurrentUserId();
        boolean result = attendanceService.register(dto, currentUserId);
        return Result.success(result);
    }

    @Operation(summary = "批量出勤登记", description = "按班级批量登记出勤")
    @PostMapping("/batch")
    @OperLog(module = "出勤管理", operation = "批量出勤登记", type = OperationTypeEnum.INSERT)
    public Result<Boolean> batchRegister(@Valid @RequestBody AttendanceBatchDTO dto) {
        Long currentUserId = getCurrentUserId();
        boolean result = attendanceService.batchRegister(dto, currentUserId);
        return Result.success(result);
    }

    @Operation(summary = "修改出勤记录", description = "修改出勤记录状态")
    @PutMapping
    @OperLog(module = "出勤管理", operation = "修改出勤记录", type = OperationTypeEnum.UPDATE)
    public Result<Boolean> modify(@Valid @RequestBody AttendanceDTO dto) {
        Long currentUserId = getCurrentUserId();
        boolean result = attendanceService.modify(dto, currentUserId);
        return Result.success(result);
    }

    @Operation(summary = "查询出勤记录", description = "查询幼儿或班级某月出勤记录")
    @GetMapping("/list")
    public Result<List<AttendanceVO>> list(AttendanceQueryDTO queryDTO) {
        List<AttendanceVO> result;
        if (queryDTO.getChildId() != null) {
            result = attendanceService.getByChildAndMonth(
                    queryDTO.getChildId(),
                    queryDTO.getYear() != null ? queryDTO.getYear() : LocalDate.now().getYear(),
                    queryDTO.getMonth() != null ? queryDTO.getMonth() : LocalDate.now().getMonthValue()
            );
        } else if (queryDTO.getClassId() != null) {
            result = attendanceService.getByClassAndMonth(
                    queryDTO.getClassId(),
                    queryDTO.getYear() != null ? queryDTO.getYear() : LocalDate.now().getYear(),
                    queryDTO.getMonth() != null ? queryDTO.getMonth() : LocalDate.now().getMonthValue()
            );
        } else {
            return Result.failed("请提供幼儿ID或班级ID");
        }
        return Result.success(result);
    }

    @Operation(summary = "出勤统计", description = "获取幼儿或班级某月出勤统计")
    @GetMapping("/statistics")
    public Result<Object> statistics(AttendanceQueryDTO queryDTO) {
        if (queryDTO.getChildId() != null) {
            AttendanceStatisticsVO result = attendanceService.getStatistics(
                    queryDTO.getChildId(),
                    queryDTO.getYear() != null ? queryDTO.getYear() : LocalDate.now().getYear(),
                    queryDTO.getMonth() != null ? queryDTO.getMonth() : LocalDate.now().getMonthValue()
            );
            return Result.success(result);
        } else if (queryDTO.getClassId() != null) {
            List<AttendanceStatisticsVO> result = attendanceService.getClassStatistics(
                    queryDTO.getClassId(),
                    queryDTO.getYear() != null ? queryDTO.getYear() : LocalDate.now().getYear(),
                    queryDTO.getMonth() != null ? queryDTO.getMonth() : LocalDate.now().getMonthValue()
            );
            return Result.success(result);
        } else {
            return Result.failed("请提供幼儿ID或班级ID");
        }
    }

    @Operation(summary = "导出出勤记录", description = "导出班级某月出勤记录Excel")
    @GetMapping("/export")
    @OperLog(module = "出勤管理", operation = "导出出勤记录", type = OperationTypeEnum.EXPORT)
    public void export(
            @Parameter(description = "班级ID") @RequestParam Long classId,
            @Parameter(description = "年份") @RequestParam Integer year,
            @Parameter(description = "月份") @RequestParam Integer month,
            HttpServletResponse response) {
        try {
            byte[] data = attendanceService.export(classId, year, month);
            String fileName = URLEncoder.encode("出勤记录_" + year + "年" + month + "月.xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.getOutputStream().write(data);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    private Long getCurrentUserId() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }
        return user.getId();
    }
}
