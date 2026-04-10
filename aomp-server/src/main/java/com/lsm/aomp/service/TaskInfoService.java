package com.lsm.aomp.service;

import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.TaskCreateDTO;
import com.lsm.aomp.vo.TaskInfoVO;

public interface TaskInfoService {

    Result<PageResult<TaskInfoVO>> page(Integer pageNum, Integer pageSize, String keyword, String status, String createBy);

    Result<TaskInfoVO> getById(Long id);

    Result<TaskInfoVO> create(TaskCreateDTO dto);

    Result<Void> execute(Long taskId);

    Result<Void> pause(Long taskId);

    Result<Void> resume(Long taskId);

    Result<Void> terminate(Long taskId);

    Result<Void> retry(Long taskId);

    Result<PageResult<TaskInfoVO>> history(Integer pageNum, Integer pageSize, String keyword, String status);

    Result<PageResult<com.lsm.aomp.entity.TaskExecLog>> getExecLogs(Long taskId, Long hostId, Integer pageNum, Integer pageSize);
}
