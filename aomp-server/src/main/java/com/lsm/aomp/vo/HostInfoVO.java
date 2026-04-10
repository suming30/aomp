package com.lsm.aomp.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HostInfoVO {

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
    private Long managerId;
    private String managerName;
    private LocalDateTime onlineTime;
    private String status;
    private LocalDateTime lastCheckTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Long> groupIds;
    private List<String> groupNames;
    private List<Long> tagIds;
    private List<String> tagNames;
}
