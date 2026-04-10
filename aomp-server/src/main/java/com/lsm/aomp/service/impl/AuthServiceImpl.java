package com.lsm.aomp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.lsm.aomp.common.BusinessException;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.common.ResultCode;
import com.lsm.aomp.dto.LoginDTO;
import com.lsm.aomp.entity.*;
import com.lsm.aomp.mapper.*;
import com.lsm.aomp.service.AuthService;
import com.lsm.aomp.util.JwtUtil;
import com.lsm.aomp.util.UserContext;
import com.lsm.aomp.vo.LoginVO;
import com.lsm.aomp.vo.RoleInfoVO;
import com.lsm.aomp.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final AuditLogMapper auditLogMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;

    private static final String LOGIN_FAIL_PREFIX = "aomp:login_fail:";
    private static final int MAX_LOGIN_RETRY = 5;
    private static final int LOCK_MINUTES = 15;

    @Override
    public Result<LoginVO> login(LoginDTO dto, String ip) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserAccount, dto.getAccount()));
        if (user == null) {
            return Result.error(ResultCode.USER_LOGIN_FAILED);
        }

        if ("disabled".equals(user.getStatus())) {
            return Result.error(ResultCode.USER_ACCOUNT_DISABLED);
        }

        if ("locked".equals(user.getStatus())) {
            return Result.error(ResultCode.USER_ACCOUNT_LOCKED);
        }

        String failKey = LOGIN_FAIL_PREFIX + user.getId();
        String failCountStr = redisTemplate.opsForValue().get(failKey);
        if (failCountStr != null && Integer.parseInt(failCountStr) >= MAX_LOGIN_RETRY) {
            user.setStatus("locked");
            user.setLockTime(LocalDateTime.now());
            sysUserMapper.updateById(user);
            return Result.error(ResultCode.USER_ACCOUNT_LOCKED);
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            redisTemplate.opsForValue().increment(failKey);
            if (failCountStr == null) {
                redisTemplate.expire(failKey, LOCK_MINUTES, TimeUnit.MINUTES);
            }
            return Result.error(ResultCode.USER_LOGIN_FAILED);
        }

        redisTemplate.delete(failKey);

        user.setLastLoginIp(ip);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLoginFailCount(0);
        sysUserMapper.updateById(user);

        List<String> permissions = getUserPermissions(user.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("permissions", permissions);
        String token = jwtUtil.generateToken(String.valueOf(user.getId()), user.getUserAccount(), claims);

        UserInfoVO userInfo = buildUserInfo(user, permissions);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(userInfo);
        loginVO.setPermissions(permissions);

        saveLoginAudit(user, ip, true);
        return Result.success(loginVO);
    }

    @Override
    public Result<Void> logout(String userId) {
        return Result.success();
    }

    @Override
    public Result<LoginVO> refreshToken(String userId) {
        SysUser user = sysUserMapper.selectById(Long.valueOf(userId));
        if (user == null) {
            return Result.error(ResultCode.UNAUTHORIZED);
        }
        List<String> permissions = getUserPermissions(user.getId());
        Map<String, Object> claims = new HashMap<>();
        claims.put("permissions", permissions);
        String token = jwtUtil.generateToken(String.valueOf(user.getId()), user.getUserAccount(), claims);

        UserInfoVO userInfo = buildUserInfo(user, permissions);
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(userInfo);
        loginVO.setPermissions(permissions);
        return Result.success(loginVO);
    }

    private List<String> getUserPermissions(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
        List<Long> enabledRoleIds = roles.stream()
                .filter(r -> "enabled".equals(r.getStatus()))
                .map(SysRole::getId).collect(Collectors.toList());
        if (enabledRoleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<SysRolePermission> rolePerms = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getRoleId, enabledRoleIds));
        if (rolePerms.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> permIds = rolePerms.stream().map(SysRolePermission::getPermissionId).distinct().collect(Collectors.toList());
        List<SysPermission> perms = sysPermissionMapper.selectBatchIds(permIds);
        return perms.stream()
                .filter(p -> Boolean.TRUE.equals(p.getVisible()))
                .map(SysPermission::getPermissionCode)
                .collect(Collectors.toList());
    }

    private UserInfoVO buildUserInfo(SysUser user, List<String> permissions) {
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
        vo.setPermissions(permissions);

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
        }
        return vo;
    }

    private void saveLoginAudit(SysUser user, String ip, boolean success) {
        AuditLog log = new AuditLog();
        log.setUserId(user.getId());
        log.setUsername(user.getUsername());
        log.setModule("auth");
        log.setAction("login");
        log.setLevel("normal");
        log.setIp(ip);
        log.setResult(success ? "success" : "failed");
        log.setDetail("User " + user.getUserAccount() + " login");
        auditLogMapper.insert(log);
    }
}
