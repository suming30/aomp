package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inspection_task_host")
public class InspectionTaskHost {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long inspectionTaskId;
    private Long hostId;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
