package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task_info")
public class TaskInfo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String taskName;
    private Long scriptId;
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
    @TableLogic
    private Integer deleted;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
