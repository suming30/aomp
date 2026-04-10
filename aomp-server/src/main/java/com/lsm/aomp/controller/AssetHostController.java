package com.lsm.aomp.controller;

import com.lsm.aomp.aop.Audit;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.HostCreateDTO;
import com.lsm.aomp.dto.HostUpdateDTO;
import com.lsm.aomp.service.AssetHostService;
import com.lsm.aomp.vo.HostInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hosts")
@RequiredArgsConstructor
public class AssetHostController {

    private final AssetHostService assetHostService;

    @GetMapping
    @PreAuthorize("hasAuthority('asset:host:list')")
    public Result<PageResult<HostInfoVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String osType,
            @RequestParam(required = false) Long managerId) {
        return assetHostService.page(pageNum, pageSize, keyword, status, osType, managerId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:host:list')")
    public Result<HostInfoVO> getById(@PathVariable Long id) {
        return assetHostService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('asset:host:add')")
    @Audit(module = "host", action = "create", level = "important")
    public Result<Void> create(@Valid @RequestBody HostCreateDTO dto) {
        return assetHostService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:host:edit')")
    @Audit(module = "host", action = "update", level = "important")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody HostUpdateDTO dto) {
        return assetHostService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:host:delete')")
    @Audit(module = "host", action = "delete", level = "critical")
    public Result<Void> delete(@PathVariable Long id) {
        return assetHostService.delete(id);
    }

    @PostMapping("/{id}/check")
    @PreAuthorize("hasAuthority('asset:host:check')")
    public Result<Void> checkConnectivity(@PathVariable Long id) {
        return assetHostService.checkConnectivity(id);
    }

    @PostMapping("/batch-check")
    @PreAuthorize("hasAuthority('asset:host:check')")
    public Result<Void> batchCheckConnectivity(@RequestBody List<Long> ids) {
        return assetHostService.batchCheckConnectivity(ids);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('asset:host:import')")
    @Audit(module = "host", action = "import", level = "important")
    public Result<Void> batchImport(@RequestBody List<HostCreateDTO> hosts) {
        return assetHostService.batchImport(hosts);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('asset:host:export')")
    public Result<List<HostInfoVO>> export(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return assetHostService.export(status, keyword);
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAuthority('asset:host:list')")
    public Result<List<HostInfoVO>> listByGroupId(@PathVariable Long groupId) {
        return assetHostService.listByGroupId(groupId);
    }

    @GetMapping("/tag/{tagId}")
    @PreAuthorize("hasAuthority('asset:host:list')")
    public Result<List<HostInfoVO>> listByTagId(@PathVariable Long tagId) {
        return assetHostService.listByTagId(tagId);
    }
}
