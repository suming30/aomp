package com.lsm.aomp.vo;

import lombok.Data;

@Data
public class RoleInfoVO {

    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private String roleType;
    private String dataScope;
    private String status;
    private Integer userCount;
}
