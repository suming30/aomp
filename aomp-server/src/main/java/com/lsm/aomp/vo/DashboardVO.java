package com.lsm.aomp.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {

    private long hostTotal;
    private long hostOnline;
    private long hostOffline;
    private long scriptTotal;
    private long taskTotal;
    private long taskRunning;
    private long taskToday;
    private long inspectionTotal;
    private long inspectionWarning;
    private long userTotal;
    private long approvalPending;
    private List<Map<String, Object>> recentTasks;
    private List<Map<String, Object>> recentAlerts;
}
