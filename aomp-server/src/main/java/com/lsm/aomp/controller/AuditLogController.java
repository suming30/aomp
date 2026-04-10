package com.lsm.aomp.controller;

import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.entity.AuditLog;
import com.lsm.aomp.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('audit:list')")
    public Result<PageResult<AuditLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return auditLogService.page(pageNum, pageSize, module, action, level, username, startTime, endTime);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('audit:export')")
    public Result<Void> export(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return auditLogService.export(module, level, startTime, endTime);
    }
}
