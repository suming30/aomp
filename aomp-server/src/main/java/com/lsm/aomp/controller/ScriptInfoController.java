package com.lsm.aomp.controller;

import com.lsm.aomp.aop.Audit;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.ScriptCreateDTO;
import com.lsm.aomp.service.ScriptInfoService;
import com.lsm.aomp.vo.ScriptInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scripts")
@RequiredArgsConstructor
public class ScriptInfoController {

    private final ScriptInfoService scriptInfoService;

    @GetMapping
    @PreAuthorize("hasAuthority('script:list')")
    public Result<PageResult<ScriptInfoVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String scriptType,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String createBy) {
        return scriptInfoService.page(pageNum, pageSize, keyword, scriptType, scope, createBy);
    }

    @GetMapping("/{id}")
    public Result<ScriptInfoVO> getById(@PathVariable Long id) {
        return scriptInfoService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('script:add')")
    @Audit(module = "script", action = "create", level = "normal")
    public Result<Void> create(@Valid @RequestBody ScriptCreateDTO dto) {
        return scriptInfoService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('script:edit')")
    @Audit(module = "script", action = "update", level = "normal")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ScriptCreateDTO dto) {
        return scriptInfoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('script:delete')")
    @Audit(module = "script", action = "delete", level = "important")
    public Result<Void> delete(@PathVariable Long id) {
        return scriptInfoService.delete(id);
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('script:edit')")
    @Audit(module = "script", action = "publish", level = "important")
    public Result<Void> publish(@PathVariable Long id) {
        return scriptInfoService.publish(id);
    }

    @PostMapping("/{id}/audit")
    @PreAuthorize("hasAuthority('script:audit')")
    @Audit(module = "script", action = "audit", level = "important")
    public Result<Void> audit(@PathVariable Long id,
                              @RequestParam String status,
                              @RequestParam(required = false) String remark) {
        return scriptInfoService.audit(id, status, remark);
    }

    @PostMapping("/{id}/copy")
    @PreAuthorize("hasAuthority('script:add')")
    @Audit(module = "script", action = "copy", level = "normal")
    public Result<Void> copy(@PathVariable Long id, @RequestParam String newName) {
        return scriptInfoService.copy(id, newName);
    }

    @GetMapping("/{id}/draft")
    public Result<ScriptInfoVO> getDraft(@PathVariable Long id) {
        return scriptInfoService.getDraft(id);
    }

    @PutMapping("/{id}/draft")
    public Result<Void> autoSaveDraft(@PathVariable Long id, @RequestBody String content) {
        return scriptInfoService.autoSaveDraft(id, content);
    }
}
