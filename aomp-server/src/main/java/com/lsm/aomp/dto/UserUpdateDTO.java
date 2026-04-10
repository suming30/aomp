package com.lsm.aomp.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateDTO {

    @Size(min = 2, max = 20, message = "用户名长度2-20字符")
    private String username;

    private String email;

    private List<Long> roleIds;

    private String status;

    private Long managerId;

    @Size(max = 200, message = "备注不超过200字符")
    private String remark;
}
