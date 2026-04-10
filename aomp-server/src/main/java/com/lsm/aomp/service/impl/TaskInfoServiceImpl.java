package com.lsm.aomp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsm.aomp.common.BusinessException;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.common.ResultCode;
import com.lsm.aomp.dto.TaskCreateDTO;
import com.lsm.aomp.entity.*;
import com.lsm.aomp.mapper.*;
import com.lsm.aomp.service.TaskInfoService;
import com.lsm.aomp.util.UserContext;
import com.lsm.aomp.vo.TaskInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskInfoServiceImpl implements TaskInfoService {

    private final TaskInfoMapper taskInfoMapper;
    private final TaskHostRelMapper taskHostRelMapper;
    private final TaskExecLogMapper taskExecLogMapper;
    private final TaskApprovalMapper taskApprovalMapper;
    private final ScriptInfoMapper scriptInfoMapper;
    private final AssetHostMapper assetHostMapper;
    private final AssetHostGroupRelMapper assetHostGroupRelMapper;

    @Override
    public Result<PageResult<TaskInfoVO>> page(Integer pageNum, Integer pageSize, String keyword, String status, String createBy) {
        Page<TaskInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) wrapper.like(TaskInfo::getTaskName, keyword);
        if (StrUtil.isNotBlank(status)) wrapper.eq(TaskInfo::getStatus, status);
        if (StrUtil.isNotBlank(createBy)) wrapper.eq(TaskInfo::getCreateBy, createBy);
        wrapper.orderByDesc(TaskInfo::getCreateTime);

        Page<TaskInfo> result = taskInfoMapper.selectPage(page, wrapper);
        List<TaskInfoVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(voList, result.getTotal(), pageNum, pageSize));
    }

    @Override
    public Result<TaskInfoVO> getById(Long id) {
        TaskInfo task = taskInfoMapper.selectById(id);
        if (task == null) return Result.error(ResultCode.NOT_FOUND);
        return Result.success(convertToVO(task));
    }

    @Override
    @Transactional
    public Result<TaskInfoVO> create(TaskCreateDTO dto) {
        ScriptInfo script = scriptInfoMapper.selectById(dto.getScriptId());
        if (script == null) return Result.error(ResultCode.NOT_FOUND);

        Set<Long> hostIds = new HashSet<>();
        if (dto.getHostIds() != null) hostIds.addAll(dto.getHostIds());
        if (dto.getHostGroupIds() != null) {
            for (Long groupId : dto.getHostGroupIds()) {
                List<AssetHostGroupRel> rels = assetHostGroupRelMapper.selectList(
                        new LambdaQueryWrapper<AssetHostGroupRel>().eq(AssetHostGroupRel::getGroupId, groupId));
                rels.forEach(r -> hostIds.add(r.getHostId()));
            }
        }

        TaskInfo task = new TaskInfo();
        task.setTaskName(dto.getTaskName());
        task.setScriptId(dto.getScriptId());
        task.setScriptVersion(dto.getScriptVersion());
        task.setExecuteMode(dto.getExecuteMode());
        task.setBatchSize(dto.getBatchSize());
        task.setBatchInterval(dto.getBatchInterval());
        task.setTaskParams(dto.getTaskParams());
        task.setTimeout(dto.getTimeout());
        task.setIsDryRun(dto.getIsDryRun());
        task.setIsSilent(dto.getIsSilent());
        task.setStatus("pending");
        task.setTotalHosts(hostIds.size());
        task.setSuccessHosts(0);
        task.setFailHosts(0);
        task.setRunningHosts(0);
        task.setCreateBy(UserContext.getCurrentUsername());
        taskInfoMapper.insert(task);

        for (Long hostId : hostIds) {
            TaskHostRel rel = new TaskHostRel();
            rel.setTaskId(task.getId());
            rel.setHostId(hostId);
            rel.setStatus("pending");
            taskHostRelMapper.insert(rel);
        }

        return Result.success(convertToVO(task));
    }

    @Override
    @Transactional
    public Result<Void> execute(Long taskId) {
        TaskInfo task = taskInfoMapper.selectById(taskId);
        if (task == null) return Result.error(ResultCode.NOT_FOUND);
        if ("running".equals(task.getStatus())) {
            throw new BusinessException(ResultCode.TASK_ALREADY_RUNNING);
        }

        task.setStatus("running");
        task.setStartTime(LocalDateTime.now());
        task.setRunningHosts(task.getTotalHosts());
        taskInfoMapper.updateById(task);

        executeAsync(taskId);
        return Result.success();
    }

    @Async
    public void executeAsync(Long taskId) {
        TaskInfo task = taskInfoMapper.selectById(taskId);
        if (task == null) return;

        ScriptInfo script = scriptInfoMapper.selectById(task.getScriptId());
        if (script == null) return;

        List<TaskHostRel> hostRels = taskHostRelMapper.selectList(
                new LambdaQueryWrapper<TaskHostRel>().eq(TaskHostRel::getTaskId, taskId));

        int successCount = 0;
        int failCount = 0;

        for (TaskHostRel rel : hostRels) {
            if (!"running".equals(taskInfoMapper.selectById(taskId).getStatus())) {
                break;
            }

            AssetHost host = assetHostMapper.selectById(rel.getHostId());
            if (host == null || "offline".equals(host.getStatus())) {
                rel.setStatus("failed");
                rel.setExitCode(-1);
                failCount++;
                taskHostRelMapper.updateById(rel);
                continue;
            }

            rel.setStatus("running");
            rel.setStartTime(LocalDateTime.now());
            taskHostRelMapper.updateById(rel);

            try {
                String command = buildCommand(script, task.getTaskParams());
                String result = executeOnHost(host, command, task.getTimeout());

                TaskExecLog logEntry = new TaskExecLog();
                logEntry.setTaskId(taskId);
                logEntry.setHostId(host.getId());
                logEntry.setLogType(result.startsWith("ERROR:") ? "stderr" : "stdout");
                logEntry.setContent(result);
                taskExecLogMapper.insert(logEntry);

                if (result.startsWith("ERROR:")) {
                    rel.setStatus("failed");
                    rel.setExitCode(1);
                    failCount++;
                } else {
                    rel.setStatus("success");
                    rel.setExitCode(0);
                    successCount++;
                }
            } catch (Exception e) {
                rel.setStatus("failed");
                rel.setExitCode(-1);
                failCount++;

                TaskExecLog logEntry = new TaskExecLog();
                logEntry.setTaskId(taskId);
                logEntry.setHostId(host.getId());
                logEntry.setLogType("stderr");
                logEntry.setContent(e.getMessage());
                taskExecLogMapper.insert(logEntry);
            }

            rel.setEndTime(LocalDateTime.now());
            if (rel.getStartTime() != null && rel.getEndTime() != null) {
                rel.setDuration((int) (java.time.Duration.between(rel.getStartTime(), rel.getEndTime()).getSeconds()));
            }
            taskHostRelMapper.updateById(rel);
        }

        task = taskInfoMapper.selectById(taskId);
        if (task != null && "running".equals(task.getStatus())) {
            task.setStatus("completed");
            task.setSuccessHosts(successCount);
            task.setFailHosts(failCount);
            task.setRunningHosts(0);
            task.setEndTime(LocalDateTime.now());
            taskInfoMapper.updateById(task);
        }
    }

    @Override
    public Result<Void> pause(Long taskId) {
        TaskInfo task = taskInfoMapper.selectById(taskId);
        if (task == null || !"running".equals(task.getStatus())) {
            throw new BusinessException(ResultCode.TASK_CANNOT_PAUSE);
        }
        task.setStatus("paused");
        taskInfoMapper.updateById(task);
        return Result.success();
    }

    @Override
    public Result<Void> resume(Long taskId) {
        TaskInfo task = taskInfoMapper.selectById(taskId);
        if (task == null || !"paused".equals(task.getStatus())) {
            throw new BusinessException(ResultCode.TASK_CANNOT_PAUSE);
        }
        task.setStatus("running");
        taskInfoMapper.updateById(task);
        return Result.success();
    }

    @Override
    public Result<Void> terminate(Long taskId) {
        TaskInfo task = taskInfoMapper.selectById(taskId);
        if (task == null || !("running".equals(task.getStatus()) || "paused".equals(task.getStatus()))) {
            throw new BusinessException(ResultCode.TASK_CANNOT_TERMINATE);
        }
        task.setStatus("terminated");
        task.setEndTime(LocalDateTime.now());
        taskInfoMapper.updateById(task);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> retry(Long taskId) {
        TaskInfo task = taskInfoMapper.selectById(taskId);
        if (task == null) return Result.error(ResultCode.NOT_FOUND);

        List<TaskHostRel> failedRels = taskHostRelMapper.selectList(
                new LambdaQueryWrapper<TaskHostRel>().eq(TaskHostRel::getTaskId, taskId).eq(TaskHostRel::getStatus, "failed"));

        for (TaskHostRel rel : failedRels) {
            rel.setStatus("pending");
            rel.setExitCode(null);
            rel.setStartTime(null);
            rel.setEndTime(null);
            rel.setDuration(null);
            taskHostRelMapper.updateById(rel);
        }

        task.setStatus("running");
        task.setFailHosts(0);
        task.setRunningHosts(failedRels.size());
        taskInfoMapper.updateById(task);

        executeAsync(taskId);
        return Result.success();
    }

    @Override
    public Result<PageResult<TaskInfoVO>> history(Integer pageNum, Integer pageSize, String keyword, String status) {
        return page(pageNum, pageSize, keyword, status, null);
    }

    @Override
    public Result<PageResult<TaskExecLog>> getExecLogs(Long taskId, Long hostId, Integer pageNum, Integer pageSize) {
        Page<TaskExecLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TaskExecLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskExecLog::getTaskId, taskId);
        if (hostId != null) wrapper.eq(TaskExecLog::getHostId, hostId);
        wrapper.orderByAsc(TaskExecLog::getCreateTime);
        Page<TaskExecLog> result = taskExecLogMapper.selectPage(page, wrapper);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize));
    }

    private String buildCommand(ScriptInfo script, String taskParams) {
        String command = script.getContent();
        if (StrUtil.isNotBlank(taskParams)) {
            command = command.replace("{{params}}", taskParams);
        }
        return command;
    }

    private String executeOnHost(AssetHost host, String command, int timeout) {
        com.lsm.aomp.util.SshUtil sshUtil = new com.lsm.aomp.util.SshUtil();
        cn.hutool.crypto.symmetric.AES aes = cn.hutool.crypto.SecureUtil.aes("aomp2026secretke".getBytes());
        String password = host.getSshPassword() != null ? aes.decryptStr(host.getSshPassword()) : null;
        String key = host.getSshKey() != null ? aes.decryptStr(host.getSshKey()) : null;
        String keyPassphrase = host.getSshKeyPassphrase() != null ? aes.decryptStr(host.getSshKeyPassphrase()) : null;
        return sshUtil.executeCommand(host.getIpAddress(), host.getSshPort(), host.getSshUser(), password, key, keyPassphrase, command, timeout * 1000);
    }

    private TaskInfoVO convertToVO(TaskInfo task) {
        TaskInfoVO vo = new TaskInfoVO();
        vo.setId(task.getId());
        vo.setTaskName(task.getTaskName());
        vo.setScriptId(task.getScriptId());
        vo.setScriptVersion(task.getScriptVersion());
        vo.setExecuteMode(task.getExecuteMode());
        vo.setBatchSize(task.getBatchSize());
        vo.setBatchInterval(task.getBatchInterval());
        vo.setTaskParams(task.getTaskParams());
        vo.setTimeout(task.getTimeout());
        vo.setIsDryRun(task.getIsDryRun());
        vo.setIsSilent(task.getIsSilent());
        vo.setStatus(task.getStatus());
        vo.setTotalHosts(task.getTotalHosts());
        vo.setSuccessHosts(task.getSuccessHosts());
        vo.setFailHosts(task.getFailHosts());
        vo.setRunningHosts(task.getRunningHosts());
        vo.setStartTime(task.getStartTime());
        vo.setEndTime(task.getEndTime());
        vo.setCreateBy(task.getCreateBy());
        vo.setCreateTime(task.getCreateTime());

        ScriptInfo script = scriptInfoMapper.selectById(task.getScriptId());
        if (script != null) vo.setScriptName(script.getScriptName());
        return vo;
    }
}
