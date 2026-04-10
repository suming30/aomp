package com.lsm.aomp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.entity.*;
import com.lsm.aomp.mapper.*;
import com.lsm.aomp.service.DashboardService;
import com.lsm.aomp.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AssetHostMapper assetHostMapper;
    private final ScriptInfoMapper scriptInfoMapper;
    private final TaskInfoMapper taskInfoMapper;
    private final InspectionTaskMapper inspectionTaskMapper;
    private final SysUserMapper sysUserMapper;
    private final TaskApprovalMapper taskApprovalMapper;

    @Override
    public Result<DashboardVO> getDashboard() {
        DashboardVO vo = new DashboardVO();

        vo.setHostTotal(assetHostMapper.selectCount(new LambdaQueryWrapper<>()));
        vo.setHostOnline(assetHostMapper.selectCount(new LambdaQueryWrapper<AssetHost>().eq(AssetHost::getStatus, "online")));
        vo.setHostOffline(assetHostMapper.selectCount(new LambdaQueryWrapper<AssetHost>().eq(AssetHost::getStatus, "offline")));

        vo.setScriptTotal(scriptInfoMapper.selectCount(new LambdaQueryWrapper<>()));

        vo.setTaskTotal(taskInfoMapper.selectCount(new LambdaQueryWrapper<>()));
        vo.setTaskRunning(taskInfoMapper.selectCount(new LambdaQueryWrapper<TaskInfo>().eq(TaskInfo::getStatus, "running")));

        java.time.LocalDateTime todayStart = java.time.LocalDate.now().atStartOfDay();
        vo.setTaskToday(taskInfoMapper.selectCount(new LambdaQueryWrapper<TaskInfo>().ge(TaskInfo::getCreateTime, todayStart)));

        vo.setInspectionTotal(inspectionTaskMapper.selectCount(new LambdaQueryWrapper<>()));
        vo.setInspectionWarning(inspectionTaskMapper.selectCount(
                new LambdaQueryWrapper<InspectionTask>().gt(InspectionTask::getWarningHosts, 0)));

        vo.setUserTotal(sysUserMapper.selectCount(new LambdaQueryWrapper<>()));
        vo.setApprovalPending(taskApprovalMapper.selectCount(
                new LambdaQueryWrapper<TaskApproval>().eq(TaskApproval::getStatus, "pending")));

        List<TaskInfo> recentTasks = taskInfoMapper.selectList(
                new LambdaQueryWrapper<TaskInfo>().orderByDesc(TaskInfo::getCreateTime).last("LIMIT 5"));
        vo.setRecentTasks(recentTasks.stream().map(t -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", t.getId());
            map.put("taskName", t.getTaskName());
            map.put("status", t.getStatus());
            map.put("createTime", t.getCreateTime());
            return map;
        }).collect(Collectors.toList()));

        vo.setRecentAlerts(Collections.emptyList());

        return Result.success(vo);
    }
}
