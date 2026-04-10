package com.lsm.aomp.controller;

import com.lsm.aomp.aop.Audit;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.PasswordChangeDTO;
import com.lsm.aomp.dto.UserCreateDTO;
import com.lsm.aomp.dto.UserUpdateDTO;
import com.lsm.aomp.service.SysUserService;
import com.lsm.aomp.util.UserContext;
import com.lsm.aomp.vo.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping
    @PreAuthorize("hasAuthority('permission:user:list')")
    public Result<PageResult<UserInfoVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String roleCode) {
        return sysUserService.page(pageNum, pageSize, account, username, status, roleCode);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:user:list')")
    public Result<UserInfoVO> getById(@PathVariable Long id) {
        return sysUserService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:user:add')")
    @Audit(module = "user", action = "create", level = "important")
    public Result<Void> create(@Valid @RequestBody UserCreateDTO dto) {
        return sysUserService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:user:edit')")
    @Audit(module = "user", action = "update", level = "important")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return sysUserService.update(id, dto);
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('permission:user:disable')")
    @Audit(module = "user", action = "disable", level = "important")
    public Result<Void> disable(@PathVariable Long id) {
        return sysUserService.disable(id);
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('permission:user:edit')")
    @Audit(module = "user", action = "enable", level = "normal")
    public Result<Void> enable(@PathVariable Long id) {
        return sysUserService.enable(id);
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('permission:user:resetPwd')")
    @Audit(module = "user", action = "resetPassword", level = "critical")
    public Result<Void> resetPassword(@PathVariable Long id) {
        return sysUserService.resetPassword(id);
    }

    @PutMapping("/batch-disable")
    @PreAuthorize("hasAuthority('permission:user:disable')")
    @Audit(module = "user", action = "batchDisable", level = "critical")
    public Result<Void> batchDisable(@RequestBody List<Long> ids) {
        return sysUserService.batchDisable(ids);
    }

    @GetMapping("/current")
    public Result<UserInfoVO> getCurrentUser() {
        String userId = UserContext.getCurrentUserId();
        return sysUserService.getCurrentUser(userId);
    }

    @PutMapping("/current/profile")
    @Audit(module = "user", action = "updateProfile", level = "normal")
    public Result<Void> updateProfile(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String avatar) {
        String userId = UserContext.getCurrentUserId();
        return sysUserService.updateProfile(userId, username, email, avatar);
    }

    @PutMapping("/current/password")
    @Audit(module = "user", action = "changePassword", level = "critical")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeDTO dto) {
        String userId = UserContext.getCurrentUserId();
        return sysUserService.changePassword(Long.valueOf(userId), dto);
    }
}
