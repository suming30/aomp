package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("script_info")
public class ScriptInfo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String scriptName;
    private String scriptType;
    private String scope;
    private String content;
    private String description;
    private Integer version;
    private String paramsSchema;
    private Boolean isDraft;
    private String auditStatus;
    private Long auditBy;
    private LocalDateTime auditTime;
    private String auditRemark;
    private Integer runCount;
    private Integer successCount;
    private Integer failCount;
    @TableLogic
    private Integer deleted;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
