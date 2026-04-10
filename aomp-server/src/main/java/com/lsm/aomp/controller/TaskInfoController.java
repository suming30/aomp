package com.lsm.aomp.controller;

import com.lsm.aomp.aop.Audit;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.TaskCreateDTO;
import com.lsm.aomp.entity.TaskExecLog;
import com.lsm.aomp.service.TaskInfoService;
import com.lsm.aomp.vo.TaskInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskInfoController {

    private final TaskInfoService taskInfoService;

    @GetMapping
    @PreAuthorize("hasAuthority('task:list')")
    public Result<PageResult<TaskInfoVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String createBy) {
        return taskInfoService.page(pageNum, pageSize, keyword, status, createBy);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('task:list')")
    public Result<TaskInfoVO> getById(@PathVariable Long id) {
        return taskInfoService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('task:create')")
    @Audit(module = "task", action = "create", level = "important")
    public Result<TaskInfoVO> create(@Valid @RequestBody TaskCreateDTO dto) {
        return taskInfoService.create(dto);
    }

    @PostMapping("/{id}/execute")
    @PreAuthorize("hasAuthority('task:execute')")
    @Audit(module = "task", action = "execute", level = "critical")
    public Result<Void> execute(@PathVariable Long id) {
        return taskInfoService.execute(id);
    }

    @PostMapping("/{id}/pause")
    @PreAuthorize("hasAuthority('task:execute')")
    @Audit(module = "task", action = "pause", level = "important")
    public Result<Void> pause(@PathVariable Long id) {
        return taskInfoService.pause(id);
    }

    @PostMapping("/{id}/resume")
    @PreAuthorize("hasAuthority('task:execute')")
    @Audit(module = "task", action = "resume", level = "important")
    public Result<Void> resume(@PathVariable Long id) {
        return taskInfoService.resume(id);
    }

    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasAuthority('task:execute')")
    @Audit(module = "task", action = "terminate", level = "critical")
    public Result<Void> terminate(@PathVariable Long id) {
        return taskInfoService.terminate(id);
    }

    @PostMapping("/{id}/retry")
    @PreAuthorize("hasAuthority('task:execute')")
    @Audit(module = "task", action = "retry", level = "important")
    public Result<Void> retry(@PathVariable Long id) {
        return taskInfoService.retry(id);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('task:list')")
    public Result<PageResult<TaskInfoVO>> history(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return taskInfoService.history(pageNum, pageSize, keyword, status);
    }

    @GetMapping("/{taskId}/logs")
    @PreAuthorize("hasAuthority('task:list')")
    public Result<PageResult<TaskExecLog>> getExecLogs(
            @PathVariable Long taskId,
            @RequestParam(required = false) Long hostId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "50") Integer pageSize) {
        return taskInfoService.getExecLogs(taskId, hostId, pageNum, pageSize);
    }
}
