package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inspection_item")
public class InspectionItem {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String itemName;
    private String itemCode;
    private String itemType;
    private Long scriptId;
    private String checkCommand;
    private String thresholdConfig;
    private String description;
    private Integer sortOrder;
    private Boolean enabled;
    @TableLogic
    private Integer deleted;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
