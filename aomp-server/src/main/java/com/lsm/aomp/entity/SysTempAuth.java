package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_temp_auth")
public class SysTempAuth {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String roleIds;
    private String resourceIds;
    private String reason;
    private Long authBy;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
