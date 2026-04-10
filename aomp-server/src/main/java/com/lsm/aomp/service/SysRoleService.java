package com.lsm.aomp.service;

import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.RoleCreateDTO;
import com.lsm.aomp.entity.SysPermission;
import com.lsm.aomp.vo.RoleInfoVO;

import java.util.List;

public interface SysRoleService {

    Result<PageResult<RoleInfoVO>> page(Integer pageNum, Integer pageSize, String roleName, String status);

    Result<List<RoleInfoVO>> listAll();

    Result<Void> create(RoleCreateDTO dto);

    Result<Void> update(Long id, RoleCreateDTO dto);

    Result<Void> delete(Long id);

    Result<Void> disable(Long id);

    Result<List<SysPermission>> permissionTree();

    Result<List<Long>> getRolePermissions(Long roleId);

    Result<Void> assignPermissions(Long roleId, List<Long> permissionIds);
}
