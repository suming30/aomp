package com.lsm.aomp.controller;

import com.lsm.aomp.aop.Audit;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.RoleCreateDTO;
import com.lsm.aomp.entity.SysPermission;
import com.lsm.aomp.service.SysRoleService;
import com.lsm.aomp.vo.RoleInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping
    @PreAuthorize("hasAuthority('permission:role:list')")
    public Result<PageResult<RoleInfoVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String status) {
        return sysRoleService.page(pageNum, pageSize, roleName, status);
    }

    @GetMapping("/all")
    public Result<List<RoleInfoVO>> listAll() {
        return sysRoleService.listAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:role:add')")
    @Audit(module = "role", action = "create", level = "important")
    public Result<Void> create(@Valid @RequestBody RoleCreateDTO dto) {
        return sysRoleService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:role:edit')")
    @Audit(module = "role", action = "update", level = "important")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RoleCreateDTO dto) {
        return sysRoleService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:role:delete')")
    @Audit(module = "role", action = "delete", level = "critical")
    public Result<Void> delete(@PathVariable Long id) {
        return sysRoleService.delete(id);
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('permission:role:edit')")
    @Audit(module = "role", action = "disable", level = "important")
    public Result<Void> disable(@PathVariable Long id) {
        return sysRoleService.disable(id);
    }

    @GetMapping("/permissions/tree")
    @PreAuthorize("hasAuthority('permission:role:list')")
    public Result<List<SysPermission>> permissionTree() {
        return sysRoleService.permissionTree();
    }

    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('permission:role:list')")
    public Result<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        return sysRoleService.getRolePermissions(roleId);
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('permission:role:edit')")
    @Audit(module = "role", action = "assignPermissions", level = "critical")
    public Result<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        return sysRoleService.assignPermissions(roleId, permissionIds);
    }
}
