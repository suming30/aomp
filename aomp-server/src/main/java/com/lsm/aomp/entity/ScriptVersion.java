package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("script_version")
public class ScriptVersion {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long scriptId;
    private Integer versionNo;
    private String content;
    private String changeLog;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
