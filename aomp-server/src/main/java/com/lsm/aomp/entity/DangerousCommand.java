package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dangerous_command")
public class DangerousCommand {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String commandPattern;
    private String commandName;
    private String level;
    private String description;
    private Boolean enabled;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
