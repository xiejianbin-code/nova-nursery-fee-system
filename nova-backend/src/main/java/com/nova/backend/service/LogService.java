package com.nova.backend.service;

import com.nova.backend.entity.OperationLog;

public interface LogService {

    Object pageList(Integer page, Integer limit, String username, String module, 
                    String startTime, String endTime, String operationType, String operationStatus);

    void saveLog(OperationLog operationLog);

    void saveLogAsync(OperationLog operationLog);

    void deleteByIds(Long[] ids);

    void clearLogs(Integer days);
}
