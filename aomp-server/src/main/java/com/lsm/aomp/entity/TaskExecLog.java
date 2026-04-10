package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task_exec_log")
public class TaskExecLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long taskId;
    private Long hostId;
    private String logType;
    private String content;
    private Integer lineNo;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
