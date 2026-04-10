package com.lsm.aomp.service;

import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.PasswordChangeDTO;
import com.lsm.aomp.dto.UserCreateDTO;
import com.lsm.aomp.dto.UserUpdateDTO;
import com.lsm.aomp.vo.UserInfoVO;

import java.util.List;

public interface SysUserService {

    Result<PageResult<UserInfoVO>> page(Integer pageNum, Integer pageSize, String account, String username, String status, String roleCode);

    Result<UserInfoVO> getById(Long id);

    Result<Void> create(UserCreateDTO dto);

    Result<Void> update(Long id, UserUpdateDTO dto);

    Result<Void> disable(Long id);

    Result<Void> enable(Long id);

    Result<Void> resetPassword(Long id);

    Result<Void> changePassword(Long userId, PasswordChangeDTO dto);

    Result<Void> batchDisable(List<Long> ids);

    Result<UserInfoVO> getCurrentUser(String userId);

    Result<Void> updateProfile(String userId, String username, String email, String avatar);
}
