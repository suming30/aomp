package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("asset_host_group_rel")
public class AssetHostGroupRel {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long hostId;
    private Long groupId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
