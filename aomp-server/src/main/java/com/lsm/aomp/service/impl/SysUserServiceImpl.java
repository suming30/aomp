package com.lsm.aomp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsm.aomp.common.BusinessException;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.common.ResultCode;
import com.lsm.aomp.dto.PasswordChangeDTO;
import com.lsm.aomp.dto.UserCreateDTO;
import com.lsm.aomp.dto.UserUpdateDTO;
import com.lsm.aomp.entity.*;
import com.lsm.aomp.mapper.*;
import com.lsm.aomp.service.SysUserService;
import com.lsm.aomp.vo.RoleInfoVO;
import com.lsm.aomp.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPasswordHistoryMapper sysPasswordHistoryMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<PageResult<UserInfoVO>> page(Integer pageNum, Integer pageSize, String account, String username, String status, String roleCode) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(account)) {
            wrapper.like(SysUser::getUserAccount, account);
        }
        if (StrUtil.isNotBlank(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = sysUserMapper.selectPage(page, wrapper);
        List<UserInfoVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(voList, result.getTotal(), pageNum, pageSize));
    }

    @Override
    public Result<UserInfoVO> getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.NOT_FOUND);
        }
        return Result.success(convertToVO(user));
    }

    @Override
    @Transactional
    public Result<Void> create(UserCreateDTO dto) {
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserAccount, dto.getUserAccount()));
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_EXISTS);
        }
        if (StrUtil.isNotBlank(dto.getEmail())) {
            count = sysUserMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, dto.getEmail()));
            if (count > 0) {
                throw new BusinessException(ResultCode.USER_EMAIL_EXISTS);
            }
        }

        SysUser user = new SysUser();
        user.setUserAccount(dto.getUserAccount());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setStatus("enabled");
        user.setForceChangePwd(true);
        user.setManagerId(dto.getManagerId());
        user.setRemark(dto.getRemark());
        user.setCreateBy(com.lsm.aomp.util.UserContext.getCurrentUsername());

        String rawPwd = StrUtil.isNotBlank(dto.getPassword()) ? dto.getPassword() : generateRandomPassword();
        user.setPassword(passwordEncoder.encode(rawPwd));

        sysUserMapper.insert(user);

        if (dto.getRoleIds() != null) {
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
            }
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> update(Long id, UserUpdateDTO dto) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.NOT_FOUND);
        }
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        if (dto.getManagerId() != null) user.setManagerId(dto.getManagerId());
        if (dto.getRemark() != null) user.setRemark(dto.getRemark());
        sysUserMapper.updateById(user);

        if (dto.getRoleIds() != null) {
            sysUserRoleMapper.delete(
                    new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(id);
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
            }
        }
        return Result.success();
    }

    @Override
    public Result<Void> disable(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) return Result.error(ResultCode.NOT_FOUND);
        user.setStatus("disabled");
        sysUserMapper.updateById(user);
        return Result.success();
    }

    @Override
    public Result<Void> enable(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) return Result.error(ResultCode.NOT_FOUND);
        user.setStatus("enabled");
        user.setLoginFailCount(0);
        sysUserMapper.updateById(user);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> resetPassword(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) return Result.error(ResultCode.NOT_FOUND);

        SysPasswordHistory history = new SysPasswordHistory();
        history.setUserId(id);
        history.setPassword(user.getPassword());
        sysPasswordHistoryMapper.insert(history);

        String newPwd = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPwd));
        user.setForceChangePwd(true);
        user.setPasswordChangeTime(java.time.LocalDateTime.now());
        sysUserMapper.updateById(user);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> changePassword(Long userId, PasswordChangeDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_MISMATCH);
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) return Result.error(ResultCode.NOT_FOUND);

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_MISMATCH);
        }

        List<SysPasswordHistory> histories = sysPasswordHistoryMapper.selectList(
                new LambdaQueryWrapper<SysPasswordHistory>()
                        .eq(SysPasswordHistory::getUserId, userId)
                        .orderByDesc(SysPasswordHistory::getCreateTime)
                        .last("LIMIT 3"));
        for (SysPasswordHistory h : histories) {
            if (passwordEncoder.matches(dto.getNewPassword(), h.getPassword())) {
                throw new BusinessException(ResultCode.USER_PASSWORD_HISTORY);
            }
        }

        SysPasswordHistory history = new SysPasswordHistory();
        history.setUserId(userId);
        history.setPassword(user.getPassword());
        sysPasswordHistoryMapper.insert(history);

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setForceChangePwd(false);
        user.setPasswordChangeTime(java.time.LocalDateTime.now());
        sysUserMapper.updateById(user);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> batchDisable(List<Long> ids) {
        for (Long id : ids) {
            SysUser user = sysUserMapper.selectById(id);
            if (user != null) {
                user.setStatus("disabled");
                sysUserMapper.updateById(user);
            }
        }
        return Result.success();
    }

    @Override
    public Result<UserInfoVO> getCurrentUser(String userId) {
        SysUser user = sysUserMapper.selectById(Long.valueOf(userId));
        if (user == null) return Result.error(ResultCode.NOT_FOUND);
        return Result.success(convertToVO(user));
    }

    @Override
    public Result<Void> updateProfile(String userId, String username, String email, String avatar) {
        SysUser user = sysUserMapper.selectById(Long.valueOf(userId));
        if (user == null) return Result.error(ResultCode.NOT_FOUND);
        if (username != null) user.setUsername(username);
        if (email != null) user.setEmail(email);
        if (avatar != null) user.setAvatar(avatar);
        sysUserMapper.updateById(user);
        return Result.success();
    }

    private UserInfoVO convertToVO(SysUser user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUserAccount(user.getUserAccount());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setForceChangePwd(user.getForceChangePwd());
        vo.setLastLoginIp(user.getLastLoginIp());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setManagerId(user.getManagerId());
        vo.setRemark(user.getRemark());
        vo.setCreateTime(user.getCreateTime());

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
        if (!userRoles.isEmpty()) {
            List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
            vo.setRoles(roles.stream().map(r -> {
                RoleInfoVO rv = new RoleInfoVO();
                rv.setId(r.getId());
                rv.setRoleName(r.getRoleName());
                rv.setRoleCode(r.getRoleCode());
                rv.setDescription(r.getDescription());
                rv.setRoleType(r.getRoleType());
                rv.setDataScope(r.getDataScope());
                rv.setStatus(r.getStatus());
                return rv;
            }).collect(Collectors.toList()));
        } else {
            vo.setRoles(Collections.emptyList());
        }
        return vo;
    }

    private String generateRandomPassword() {
        return "Aomp@" + cn.hutool.core.util.RandomUtil.randomString(8);
    }
}
