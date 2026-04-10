package com.lsm.aomp.controller;

import com.lsm.aomp.aop.Audit;
import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.entity.InspectionItem;
import com.lsm.aomp.entity.InspectionResult;
import com.lsm.aomp.entity.InspectionTask;
import com.lsm.aomp.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    @GetMapping("/items")
    @PreAuthorize("hasAuthority('inspection:list')")
    public Result<PageResult<InspectionItem>> itemPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String itemType) {
        return inspectionService.itemPage(pageNum, pageSize, keyword, itemType);
    }

    @PostMapping("/items")
    @PreAuthorize("hasAuthority('inspection:add')")
    @Audit(module = "inspection", action = "createItem", level = "normal")
    public Result<Void> createItem(@RequestBody InspectionItem item) {
        return inspectionService.createItem(item);
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasAuthority('inspection:edit')")
    @Audit(module = "inspection", action = "updateItem", level = "normal")
    public Result<Void> updateItem(@PathVariable Long id, @RequestBody InspectionItem item) {
        return inspectionService.updateItem(id, item);
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasAuthority('inspection:delete')")
    @Audit(module = "inspection", action = "deleteItem", level = "important")
    public Result<Void> deleteItem(@PathVariable Long id) {
        return inspectionService.deleteItem(id);
    }

    @PostMapping("/execute")
    @PreAuthorize("hasAuthority('inspection:execute')")
    @Audit(module = "inspection", action = "execute", level = "critical")
    public Result<InspectionTask> executeInspection(
            @RequestParam(required = false) String taskName,
            @RequestBody InspectionExecuteRequest request) {
        return inspectionService.executeInspection(taskName, request.getHostIds(), request.getItemIds());
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasAuthority('inspection:list')")
    public Result<PageResult<InspectionTask>> taskPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String status) {
        return inspectionService.taskPage(pageNum, pageSize, status);
    }

    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasAuthority('inspection:list')")
    public Result<InspectionTask> getTaskById(@PathVariable Long id) {
        return inspectionService.getTaskById(id);
    }

    @GetMapping("/tasks/{taskId}/results")
    @PreAuthorize("hasAuthority('inspection:list')")
    public Result<List<InspectionResult>> getTaskResults(
            @PathVariable Long taskId,
            @RequestParam(required = false) Long hostId) {
        return inspectionService.getTaskResults(taskId, hostId);
    }

    @GetMapping("/tasks/{taskId}/hosts/{hostId}/results")
    @PreAuthorize("hasAuthority('inspection:list')")
    public Result<List<InspectionResult>> getHostResults(
            @PathVariable Long taskId,
            @PathVariable Long hostId) {
        return inspectionService.getHostResults(taskId, hostId);
    }

    public static class InspectionExecuteRequest {
        private List<Long> hostIds;
        private List<Long> itemIds;

        public List<Long> getHostIds() { return hostIds; }
        public void setHostIds(List<Long> hostIds) { this.hostIds = hostIds; }
        public List<Long> getItemIds() { return itemIds; }
        public void setItemIds(List<Long> itemIds) { this.itemIds = itemIds; }
    }
}
