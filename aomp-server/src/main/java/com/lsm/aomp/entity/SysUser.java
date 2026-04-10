package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String userAccount;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private String status;
    private Boolean forceChangePwd;
    private LocalDateTime passwordChangeTime;
    private String lastLoginIp;
    private LocalDateTime lastLoginTime;
    private Integer loginFailCount;
    private LocalDateTime lockTime;
    private Long managerId;
    private String remark;
    @TableLogic
    private Integer deleted;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
