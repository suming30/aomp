package com.lsm.aomp.service;

import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.ScriptCreateDTO;
import com.lsm.aomp.vo.ScriptInfoVO;

public interface ScriptInfoService {

    Result<PageResult<ScriptInfoVO>> page(Integer pageNum, Integer pageSize, String keyword, String scriptType, String scope, String createBy);

    Result<ScriptInfoVO> getById(Long id);

    Result<Void> create(ScriptCreateDTO dto);

    Result<Void> update(Long id, ScriptCreateDTO dto);

    Result<Void> delete(Long id);

    Result<Void> publish(Long id);

    Result<Void> audit(Long id, String status, String remark);

    Result<Void> copy(Long sourceId, String newName);

    Result<ScriptInfoVO> getDraft(Long id);

    Result<Void> autoSaveDraft(Long id, String content);
}
