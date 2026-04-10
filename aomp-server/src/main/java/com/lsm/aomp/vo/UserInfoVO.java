package com.lsm.aomp.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserInfoVO {

    private Long id;
    private String userAccount;
    private String username;
    private String email;
    private String avatar;
    private String status;
    private Boolean forceChangePwd;
    private String lastLoginIp;
    private LocalDateTime lastLoginTime;
    private Long managerId;
    private String remark;
    private LocalDateTime createTime;
    private List<RoleInfoVO> roles;
    private List<String> permissions;
}
