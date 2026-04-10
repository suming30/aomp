package com.lsm.aomp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.entity.AuditLog;
import com.lsm.aomp.mapper.AuditLogMapper;
import com.lsm.aomp.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogMapper auditLogMapper;

    @Override
    public Result<PageResult<AuditLog>> page(Integer pageNum, Integer pageSize, String module, String action, String level, String username, String startTime, String endTime) {
        Page<AuditLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(module)) wrapper.eq(AuditLog::getModule, module);
        if (StrUtil.isNotBlank(action)) wrapper.like(AuditLog::getAction, action);
        if (StrUtil.isNotBlank(level)) wrapper.eq(AuditLog::getLevel, level);
        if (StrUtil.isNotBlank(username)) wrapper.like(AuditLog::getUsername, username);
        if (StrUtil.isNotBlank(startTime)) {
            wrapper.ge(AuditLog::getCreateTime, LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StrUtil.isNotBlank(endTime)) {
            wrapper.le(AuditLog::getCreateTime, LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        wrapper.orderByDesc(AuditLog::getCreateTime);

        Page<AuditLog> result = auditLogMapper.selectPage(page, wrapper);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize));
    }

    @Override
    public Result<Void> export(String module, String level, String startTime, String endTime) {
        return Result.success();
    }
}
