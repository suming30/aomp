package com.lsm.aomp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class HostCreateDTO {

    @NotBlank(message = "IP地址不能为空")
    private String ipAddress;

    private Integer sshPort = 22;

    private String hostname;

    private String alias;

    private String osType;

    private Integer cpuCores;

    private Integer memoryGb;

    private Integer diskGb;

    private String sshUser;

    private String sshAuthType = "password";

    private String sshPassword;

    private String sshKey;

    private String sshKeyPassphrase;

    private Long managerId;

    private String onlineTime;

    @Size(max = 500, message = "备注不超过500字符")
    private String remark;

    private List<Long> groupIds;

    private List<Long> tagIds;
}
