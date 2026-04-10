package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_password_history")
public class SysPasswordHistory {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String password;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
