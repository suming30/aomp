package com.lsm.aomp.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskInfoVO {

    private Long id;
    private String taskName;
    private Long scriptId;
    private String scriptName;
    private Integer scriptVersion;
    private String executeMode;
    private Integer batchSize;
    private Integer batchInterval;
    private String taskParams;
    private Integer timeout;
    private Boolean isDryRun;
    private Boolean isSilent;
    private String status;
    private Integer totalHosts;
    private Integer successHosts;
    private Integer failHosts;
    private Integer runningHosts;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String createBy;
    private LocalDateTime createTime;
}
