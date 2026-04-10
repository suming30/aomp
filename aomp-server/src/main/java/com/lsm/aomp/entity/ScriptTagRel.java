package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("script_tag_rel")
public class ScriptTagRel {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long scriptId;
    private Long tagId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
