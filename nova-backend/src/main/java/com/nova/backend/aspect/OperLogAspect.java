package com.nova.backend.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.nova.backend.annotation.OperLog;
import com.nova.backend.entity.OperationLog;
import com.nova.backend.entity.User;
import com.nova.backend.service.LogService;
import com.nova.backend.service.UserService;
import com.nova.backend.util.SnowflakeIdGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    private final LogService logService;
    private final UserService userService;

    private static final int MAX_PARAM_LENGTH = 2000;
    private static final int MAX_RESULT_LENGTH = 2000;

    @Pointcut("@annotation(com.nova.backend.annotation.OperLog)")
    public void operLogPointcut() {
    }

    @AfterReturning(pointcut = "operLogPointcut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        long startTime = System.currentTimeMillis();
        try {
            handleLog(joinPoint, null, result, startTime);
        } finally {
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;
            log.debug("接口耗时: {}ms", costTime);
        }
    }

    @AfterThrowing(pointcut = "operLogPointcut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        long startTime = System.currentTimeMillis();
        try {
            handleLog(joinPoint, e, null, startTime);
        } finally {
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;
            log.debug("接口耗时: {}ms", costTime);
        }
    }

    private void handleLog(JoinPoint joinPoint, Exception e, Object result, long startTime) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperLog operLog = method.getAnnotation(OperLog.class);
            
            if (operLog == null) {
                return;
            }

            HttpServletRequest request = getRequest();
            if (request == null) {
                return;
            }

            OperationLog operationLog = new OperationLog();
            operationLog.setTraceId(SnowflakeIdGenerator.generateIdStr());
            operationLog.setOperationModule(operLog.module());
            operationLog.setOperationName(operLog.operation());
            operationLog.setOperationType(operLog.type().name());
            operationLog.setRequestMethod(request.getMethod());
            operationLog.setRequestUrl(request.getRequestURI());
            operationLog.setCreateTime(LocalDateTime.now());
            
            // 计算耗时
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;
            operationLog.setCostTime(costTime);

            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                operationLog.setOperator(currentUser.getRealName() != null ? currentUser.getRealName() : currentUser.getUsername());
                operationLog.setOperatorId(currentUser.getId());
            }

            String ip = getClientIp(request);
            operationLog.setOperationIp(ip);

            if (operLog.saveRequest()) {
                String params = getRequestParams(joinPoint);
                operationLog.setRequestParams(truncate(params, MAX_PARAM_LENGTH));
            }

            if (e == null) {
                operationLog.setOperationStatus("成功");
                if (operLog.saveResponse() && result != null) {
                    String resultStr = JSONUtil.toJsonStr(result);
                    operationLog.setResponseResult(truncate(resultStr, MAX_RESULT_LENGTH));
                }
            } else {
                operationLog.setOperationStatus("失败");
                operationLog.setErrorMsg(truncate(e.getMessage(), MAX_RESULT_LENGTH));
                operationLog.setExceptionClass(e.getClass().getName());
                operationLog.setExceptionStack(getStackTrace(e));
            }

            String userAgentStr = request.getHeader("User-Agent");
            if (StrUtil.isNotBlank(userAgentStr)) {
                UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
                operationLog.setBrowser(userAgent.getBrowser().getName() + " " + userAgent.getVersion());
                operationLog.setOs(userAgent.getOs().getName());
            }

            logService.saveLogAsync(operationLog);

        } catch (Exception ex) {
            log.error("记录操作日志异常: {}", ex.getMessage(), ex);
        }
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getRequestParams(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        
        if (paramNames == null || paramNames.length == 0) {
            return "";
        }

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValues[i];
            if (value instanceof HttpServletRequest 
                || value instanceof HttpServletResponse 
                || value instanceof MultipartFile) {
                continue;
            }
            params.put(paramNames[i], value);
        }

        try {
            return JSONUtil.toJsonStr(params);
        } catch (Exception e) {
            return params.toString();
        }
    }

    private String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength ? str.substring(0, maxLength) + "..." : str;
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = e.getStackTrace();
        int maxDepth = Math.min(stackTrace.length, 10);
        for (int i = 0; i < maxDepth; i++) {
            sb.append(stackTrace[i].toString()).append("\n");
        }
        return sb.toString();
    }
}
