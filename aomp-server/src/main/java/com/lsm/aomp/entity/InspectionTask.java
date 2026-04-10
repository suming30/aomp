package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inspection_task")
public class InspectionTask {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String taskName;
    private String status;
    private Integer totalHosts;
    private Integer normalHosts;
    private Integer warningHosts;
    private Integer errorHosts;
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
