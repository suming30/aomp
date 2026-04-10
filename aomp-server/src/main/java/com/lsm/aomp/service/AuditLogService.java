package com.lsm.aomp.service;

import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.entity.AuditLog;

public interface AuditLogService {

    Result<PageResult<AuditLog>> page(Integer pageNum, Integer pageSize, String module, String action, String level, String username, String startTime, String endTime);

    Result<Void> export(String module, String level, String startTime, String endTime);
}
