package com.lsm.aomp.service;

import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.LoginDTO;
import com.lsm.aomp.vo.LoginVO;

public interface AuthService {

    Result<LoginVO> login(LoginDTO dto, String ip);

    Result<Void> logout(String userId);

    Result<LoginVO> refreshToken(String userId);
}
