package com.lsm.aomp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserCreateDTO {

    @NotBlank(message = "登录账号不能为空")
    @Size(min = 4, max = 20, message = "账号长度4-20位")
    private String userAccount;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度2-20字符")
    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotEmpty(message = "至少选择1个角色")
    private java.util.List<Long> roleIds;

    private String password;

    private Long managerId;

    @Size(max = 200, message = "备注不超过200字符")
    private String remark;
}
