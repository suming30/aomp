package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("webhook_config")
public class WebhookConfig {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private String url;
    private String channel;
    private String secret;
    private String enabledEvents;
    private Boolean enabled;
    @TableLogic
    private Integer deleted;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
