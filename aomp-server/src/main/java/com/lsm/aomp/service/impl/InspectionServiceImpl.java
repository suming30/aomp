package com.lsm.aomp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.entity.*;
import com.lsm.aomp.mapper.*;
import com.lsm.aomp.service.InspectionService;
import com.lsm.aomp.util.SshUtil;
import com.lsm.aomp.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {

    private final InspectionItemMapper inspectionItemMapper;
    private final InspectionTaskMapper inspectionTaskMapper;
    private final InspectionTaskHostMapper inspectionTaskHostMapper;
    private final InspectionResultMapper inspectionResultMapper;
    private final AssetHostMapper assetHostMapper;
    private final SshUtil sshUtil;

    @Override
    public Result<PageResult<InspectionItem>> itemPage(Integer pageNum, Integer pageSize, String keyword, String itemType) {
        Page<InspectionItem> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InspectionItem> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) wrapper.like(InspectionItem::getItemName, keyword);
        if (StrUtil.isNotBlank(itemType)) wrapper.eq(InspectionItem::getItemType, itemType);
        wrapper.orderByAsc(InspectionItem::getSortOrder);
        Page<InspectionItem> result = inspectionItemMapper.selectPage(page, wrapper);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize));
    }

    @Override
    public Result<Void> createItem(InspectionItem item) {
        item.setItemType("custom");
        item.setCreateBy(UserContext.getCurrentUsername());
        inspectionItemMapper.insert(item);
        return Result.success();
    }

    @Override
    public Result<Void> updateItem(Long id, InspectionItem item) {
        InspectionItem existing = inspectionItemMapper.selectById(id);
        if (existing == null) return Result.error(com.lsm.aomp.common.ResultCode.NOT_FOUND);
        item.setId(id);
        inspectionItemMapper.updateById(item);
        return Result.success();
    }

    @Override
    public Result<Void> deleteItem(Long id) {
        inspectionItemMapper.deleteById(id);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<InspectionTask> executeInspection(String taskName, List<Long> hostIds, List<Long> itemIds) {
        InspectionTask task = new InspectionTask();
        task.setTaskName(StrUtil.isNotBlank(taskName) ? taskName : "Inspection-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        task.setStatus("running");
        task.setTotalHosts(hostIds.size());
        task.setNormalHosts(0);
        task.setWarningHosts(0);
        task.setErrorHosts(0);
        task.setStartTime(LocalDateTime.now());
        task.setCreateBy(UserContext.getCurrentUsername());
        inspectionTaskMapper.insert(task);

        for (Long hostId : hostIds) {
            InspectionTaskHost th = new InspectionTaskHost();
            th.setInspectionTaskId(task.getId());
            th.setHostId(hostId);
            th.setStatus("running");
            inspectionTaskHostMapper.insert(th);
        }

        executeInspectionAsync(task.getId(), hostIds, itemIds);
        return Result.success(task);
    }

    @Async
    public void executeInspectionAsync(Long taskId, List<Long> hostIds, List<Long> itemIds) {
        List<InspectionItem> items = inspectionItemMapper.selectBatchIds(itemIds);
        int normalCount = 0;
        int warningCount = 0;
        int errorCount = 0;

        for (Long hostId : hostIds) {
            AssetHost host = assetHostMapper.selectById(hostId);
            if (host == null || "offline".equals(host.getStatus())) {
                errorCount++;
                continue;
            }

            boolean hostHasWarning = false;
            boolean hostHasError = false;

            for (InspectionItem item : items) {
                InspectionResult result = new InspectionResult();
                result.setInspectionTaskId(taskId);
                result.setHostId(hostId);
                result.setItemId(item.getId());
                result.setThresholdValue(item.getThresholdConfig());

                try {
                    cn.hutool.crypto.symmetric.AES aes = SecureUtil.aes("aomp2026secretke".getBytes());
                    String password = host.getSshPassword() != null ? aes.decryptStr(host.getSshPassword()) : null;
                    String key = host.getSshKey() != null ? aes.decryptStr(host.getSshKey()) : null;
                    String keyPassphrase = host.getSshKeyPassphrase() != null ? aes.decryptStr(host.getSshKeyPassphrase()) : null;

                    String output = sshUtil.executeCommand(
                            host.getIpAddress(), host.getSshPort(), host.getSshUser(),
                            password, key, keyPassphrase,
                            item.getCheckCommand(), 30000);

                    result.setActualValue(output.trim());
                    result.setStatus(evaluateResult(output.trim(), item.getThresholdConfig()));
                    result.setDetail(output);
                } catch (Exception e) {
                    result.setStatus("error");
                    result.setDetail(e.getMessage());
                }

                if ("warning".equals(result.getStatus())) hostHasWarning = true;
                if ("error".equals(result.getStatus())) hostHasError = true;
                inspectionResultMapper.insert(result);
            }

            if (hostHasError) errorCount++;
            else if (hostHasWarning) warningCount++;
            else normalCount++;

            InspectionTaskHost th = inspectionTaskHostMapper.selectOne(
                    new LambdaQueryWrapper<InspectionTaskHost>()
                            .eq(InspectionTaskHost::getInspectionTaskId, taskId)
                            .eq(InspectionTaskHost::getHostId, hostId));
            if (th != null) {
                th.setStatus("completed");
                inspectionTaskHostMapper.updateById(th);
            }
        }

        InspectionTask task = inspectionTaskMapper.selectById(taskId);
        if (task != null) {
            task.setStatus("completed");
            task.setNormalHosts(normalCount);
            task.setWarningHosts(warningCount);
            task.setErrorHosts(errorCount);
            task.setEndTime(LocalDateTime.now());
            inspectionTaskMapper.updateById(task);
        }
    }

    @Override
    public Result<PageResult<InspectionTask>> taskPage(Integer pageNum, Integer pageSize, String status) {
        Page<InspectionTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InspectionTask> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(status)) wrapper.eq(InspectionTask::getStatus, status);
        wrapper.orderByDesc(InspectionTask::getCreateTime);
        Page<InspectionTask> result = inspectionTaskMapper.selectPage(page, wrapper);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize));
    }

    @Override
    public Result<InspectionTask> getTaskById(Long id) {
        InspectionTask task = inspectionTaskMapper.selectById(id);
        if (task == null) return Result.error(com.lsm.aomp.common.ResultCode.NOT_FOUND);
        return Result.success(task);
    }

    @Override
    public Result<List<InspectionResult>> getTaskResults(Long taskId, Long hostId) {
        LambdaQueryWrapper<InspectionResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionResult::getInspectionTaskId, taskId);
        if (hostId != null) wrapper.eq(InspectionResult::getHostId, hostId);
        List<InspectionResult> results = inspectionResultMapper.selectList(wrapper);
        return Result.success(results);
    }

    @Override
    public Result<List<InspectionResult>> getHostResults(Long taskId, Long hostId) {
        List<InspectionResult> results = inspectionResultMapper.selectList(
                new LambdaQueryWrapper<InspectionResult>()
                        .eq(InspectionResult::getInspectionTaskId, taskId)
                        .eq(InspectionResult::getHostId, hostId));
        return Result.success(results);
    }

    private String evaluateResult(String value, String thresholdConfig) {
        if (StrUtil.isBlank(thresholdConfig)) return "normal";
        try {
            double val = Double.parseDouble(value.replaceAll("[^0-9.]", ""));
            com.alibaba.fastjson2.JSONObject config = com.alibaba.fastjson2.JSON.parseObject(thresholdConfig);
            if (config.containsKey("error")) {
                double errorThreshold = config.getDoubleValue("error");
                if (val >= errorThreshold) return "error";
            }
            if (config.containsKey("warning")) {
                double warningThreshold = config.getDoubleValue("warning");
                if (val >= warningThreshold) return "warning";
            }
        } catch (Exception e) {
            log.warn("Failed to evaluate inspection result: {}", e.getMessage());
        }
        return "normal";
    }
}
