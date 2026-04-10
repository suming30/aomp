package com.lsm.aomp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("asset_host")
public class AssetHost {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String ipAddress;
    private Integer sshPort;
    private String hostname;
    private String alias;
    private String osType;
    private Integer cpuCores;
    private Integer memoryGb;
    private Integer diskGb;
    private String sshUser;
    private String sshAuthType;
    private String sshPassword;
    private String sshKey;
    private String sshKeyPassphrase;
    private Long managerId;
    private LocalDateTime onlineTime;
    private String status;
    private LocalDateTime lastCheckTime;
    private String remark;
    @TableLogic
    private Integer deleted;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
