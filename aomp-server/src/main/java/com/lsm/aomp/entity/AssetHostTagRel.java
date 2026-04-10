package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("asset_host_tag_rel")
public class AssetHostTagRel {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long hostId;
    private Long tagId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
