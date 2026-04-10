package com.lsm.aomp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RoleCreateDTO {

    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 20, message = "角色名称长度2-20字符")
    private String roleName;

    @Size(max = 100, message = "描述不超过100字符")
    private String description;

    private String dataScope;

    private List<Long> permissionIds;
}
