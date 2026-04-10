package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task_host_rel")
public class TaskHostRel {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long taskId;
    private Long hostId;
    private String status;
    private Integer exitCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
