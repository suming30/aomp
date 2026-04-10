package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("asset_host_group")
public class AssetHostGroup {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String groupName;
    private String groupType;
    private String dynamicRule;
    private String description;
    @TableLogic
    private Integer deleted;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
