package com.lsm.aomp.vo;

import lombok.Data;

import java.util.List;

@Data
public class LoginVO {

    private String token;
    private UserInfoVO user;
    private List<String> permissions;
}
