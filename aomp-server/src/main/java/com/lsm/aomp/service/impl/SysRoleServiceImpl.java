package com.lsm.aomp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsm.aomp.common.BusinessException;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.common.ResultCode;
import com.lsm.aomp.dto.RoleCreateDTO;
import com.lsm.aomp.entity.SysPermission;
import com.lsm.aomp.entity.SysRole;
import com.lsm.aomp.entity.SysRolePermission;
import com.lsm.aomp.entity.SysUserRole;
import com.lsm.aomp.mapper.SysPermissionMapper;
import com.lsm.aomp.mapper.SysRoleMapper;
import com.lsm.aomp.mapper.SysRolePermissionMapper;
import com.lsm.aomp.mapper.SysUserRoleMapper;
import com.lsm.aomp.service.SysRoleService;
import com.lsm.aomp.vo.RoleInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Result<PageResult<RoleInfoVO>> page(Integer pageNum, Integer pageSize, String roleName, String status) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(roleName)) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(SysRole::getStatus, status);
        }
        wrapper.orderByAsc(SysRole::getRoleType).orderByDesc(SysRole::getCreateTime);
        Page<SysRole> result = sysRoleMapper.selectPage(page, wrapper);
        List<RoleInfoVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(voList, result.getTotal(), pageNum, pageSize));
    }

    @Override
    public Result<List<RoleInfoVO>> listAll() {
        List<SysRole> roles = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, "enabled").orderByAsc(SysRole::getSortOrder));
        List<RoleInfoVO> voList = roles.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    @Transactional
    public Result<Void> create(RoleCreateDTO dto) {
        Long count = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleName, dto.getRoleName()));
        if (count > 0) {
            throw new BusinessException(ResultCode.ROLE_NAME_EXISTS);
        }

        SysRole role = new SysRole();
        role.setRoleName(dto.getRoleName());
        role.setRoleCode("custom_" + System.currentTimeMillis());
        role.setDescription(dto.getDescription());
        role.setRoleType("custom");
        role.setDataScope(dto.getDataScope() != null ? dto.getDataScope() : "self");
        role.setStatus("enabled");
        role.setCreateBy(com.lsm.aomp.util.UserContext.getCurrentUsername());
        sysRoleMapper.insert(role);

        if (dto.getPermissionIds() != null) {
            assignPermissionsInternal(role.getId(), dto.getPermissionIds());
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> update(Long id, RoleCreateDTO dto) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) return Result.error(ResultCode.NOT_FOUND);

        Long count = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleName, dto.getRoleName()).ne(SysRole::getId, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.ROLE_NAME_EXISTS);
        }

        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        if (dto.getDataScope() != null) role.setDataScope(dto.getDataScope());
        sysRoleMapper.updateById(role);

        if (dto.getPermissionIds() != null) {
            sysRolePermissionMapper.delete(
                    new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
            assignPermissionsInternal(id, dto.getPermissionIds());
        }
        return Result.success();
    }

    @Override
    public Result<Void> delete(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) return Result.error(ResultCode.NOT_FOUND);
        if ("builtin".equals(role.getRoleType())) {
            throw new BusinessException(ResultCode.ROLE_BUILTIN_CANNOT_DELETE);
        }
        sysRoleMapper.deleteById(id);
        return Result.success();
    }

    @Override
    public Result<Void> disable(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) return Result.error(ResultCode.NOT_FOUND);
        role.setStatus("disabled");
        sysRoleMapper.updateById(role);
        return Result.success();
    }

    @Override
    public Result<List<SysPermission>> permissionTree() {
        List<SysPermission> all = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getVisible, 1).orderByAsc(SysPermission::getSortOrder));
        return Result.success(all);
    }

    @Override
    public Result<List<Long>> getRolePermissions(Long roleId) {
        List<SysRolePermission> list = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        List<Long> permIds = list.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
        return Result.success(permIds);
    }

    @Override
    @Transactional
    public Result<Void> assignPermissions(Long roleId, List<Long> permissionIds) {
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        assignPermissionsInternal(roleId, permissionIds);
        return Result.success();
    }

    private void assignPermissionsInternal(Long roleId, List<Long> permissionIds) {
        for (Long permId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permId);
            sysRolePermissionMapper.insert(rp);
        }
    }

    private RoleInfoVO convertToVO(SysRole role) {
        RoleInfoVO vo = new RoleInfoVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setDescription(role.getDescription());
        vo.setRoleType(role.getRoleType());
        vo.setDataScope(role.getDataScope());
        vo.setStatus(role.getStatus());
        Long userCount = sysUserRoleMapper.selectCount(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, role.getId()));
        vo.setUserCount(userCount.intValue());
        return vo;
    }
}
