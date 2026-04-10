package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inspection_result")
public class InspectionResult {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long inspectionTaskId;
    private Long hostId;
    private Long itemId;
    private String status;
    private String actualValue;
    private String thresholdValue;
    private String detail;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
