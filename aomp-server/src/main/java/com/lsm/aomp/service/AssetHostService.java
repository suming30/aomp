package com.lsm.aomp.service;

import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.HostCreateDTO;
import com.lsm.aomp.dto.HostUpdateDTO;
import com.lsm.aomp.vo.HostInfoVO;

import java.util.List;

public interface AssetHostService {

    Result<PageResult<HostInfoVO>> page(Integer pageNum, Integer pageSize, String keyword, String status, String osType, Long managerId);

    Result<HostInfoVO> getById(Long id);

    Result<Void> create(HostCreateDTO dto);

    Result<Void> update(Long id, HostUpdateDTO dto);

    Result<Void> delete(Long id);

    Result<Void> checkConnectivity(Long id);

    Result<Void> batchCheckConnectivity(List<Long> ids);

    Result<Void> batchImport(List<HostCreateDTO> hosts);

    Result<List<HostInfoVO>> export(String status, String keyword);

    Result<List<HostInfoVO>> listByGroupId(Long groupId);

    Result<List<HostInfoVO>> listByTagId(Long tagId);
}
