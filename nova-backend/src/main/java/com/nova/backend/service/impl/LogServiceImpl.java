package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.entity.OperationLog;
import com.nova.backend.mapper.OperationLogMapper;
import com.nova.backend.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final OperationLogMapper operationLogMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Object pageList(Integer page, Integer limit, String username, String module,
                           String startTime, String endTime, String operationType, String operationStatus) {
        Page<OperationLog> pageInfo = new Page<>(page, limit);
        
        LambdaQueryWrapper<OperationLog> queryWrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            queryWrapper.like(OperationLog::getOperator, username);
        }
        if (module != null && !module.isEmpty()) {
            queryWrapper.like(OperationLog::getOperationModule, module);
        }
        if (operationType != null && !operationType.isEmpty()) {
            queryWrapper.eq(OperationLog::getOperationType, operationType);
        }
        if (operationStatus != null && !operationStatus.isEmpty()) {
            queryWrapper.eq(OperationLog::getOperationStatus, operationStatus);
        }
        if (startTime != null && !startTime.isEmpty()) {
            queryWrapper.ge(OperationLog::getCreateTime, LocalDateTime.parse(startTime, DATE_FORMATTER));
        }
        if (endTime != null && !endTime.isEmpty()) {
            queryWrapper.le(OperationLog::getCreateTime, LocalDateTime.parse(endTime, DATE_FORMATTER));
        }
        
        queryWrapper.orderByDesc(OperationLog::getCreateTime);
        
        return operationLogMapper.selectPage(pageInfo, queryWrapper);
    }

    @Override
    public void saveLog(OperationLog operationLog) {
        try {
            operationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage(), e);
        }
    }

    @Async("logExecutor")
    @Override
    public void saveLogAsync(OperationLog operationLog) {
        try {
            operationLogMapper.insert(operationLog);
            log.debug("异步保存操作日志成功, traceId: {}", operationLog.getTraceId());
        } catch (Exception e) {
            log.error("异步保存操作日志失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void deleteByIds(Long[] ids) {
        for (Long id : ids) {
            operationLogMapper.deleteById(id);
        }
    }

    @Override
    public void clearLogs(Integer days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<OperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(OperationLog::getCreateTime, threshold);
        operationLogMapper.delete(queryWrapper);
    }
}
