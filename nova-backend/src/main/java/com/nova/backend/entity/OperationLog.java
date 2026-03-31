package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String traceId;

    private String operator;

    private Long operatorId;

    private String operationIp;

    private String operationLocation;

    private String operationModule;

    private String operationType;

    private String operationName;

    private String requestMethod;

    private String requestUrl;

    private String requestParams;

    private String responseResult;

    private String operationStatus;

    private String errorMsg;

    private String exceptionClass;

    private String exceptionStack;

    private Long costTime;

    private String browser;

    private String os;

    private LocalDateTime createTime;
}
