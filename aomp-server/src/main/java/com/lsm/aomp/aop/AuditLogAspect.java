package com.lsm.aomp.aop;

import com.lsm.aomp.entity.AuditLog;
import com.lsm.aomp.mapper.AuditLogMapper;
import com.lsm.aomp.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogMapper auditLogMapper;

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint point, com.lsm.aomp.aop.Audit auditLog) throws Throwable {
        String userId = UserContext.getCurrentUserId();
        String username = UserContext.getCurrentUsername();

        AuditLog logEntity = new AuditLog();
        logEntity.setUserId(userId != null ? Long.valueOf(userId) : null);
        logEntity.setUsername(username);
        logEntity.setModule(auditLog.module());
        logEntity.setAction(auditLog.action());
        logEntity.setLevel(auditLog.level());
        logEntity.setResult("success");

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logEntity.setIp(getClientIp(request));
            logEntity.setUserAgent(request.getHeader("User-Agent"));
        }

        Object result;
        try {
            result = point.proceed();
        } catch (Throwable e) {
            logEntity.setResult("failed");
            logEntity.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            try {
                logEntity.setDetail(Arrays.toString(point.getArgs()));
                auditLogMapper.insert(logEntity);
            } catch (Exception e) {
                log.error("Failed to save audit log", e);
            }
        }
        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null && ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }
}
