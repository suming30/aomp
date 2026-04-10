package com.lsm.aomp.service;

import com.lsm.aomp.common.PageResult;
import com.lsm.aomp.common.Result;
import com.lsm.aomp.entity.InspectionItem;
import com.lsm.aomp.entity.InspectionResult;
import com.lsm.aomp.entity.InspectionTask;

import java.util.List;

public interface InspectionService {

    Result<PageResult<InspectionItem>> itemPage(Integer pageNum, Integer pageSize, String keyword, String itemType);

    Result<Void> createItem(InspectionItem item);

    Result<Void> updateItem(Long id, InspectionItem item);

    Result<Void> deleteItem(Long id);

    Result<InspectionTask> executeInspection(String taskName, List<Long> hostIds, List<Long> itemIds);

    Result<PageResult<InspectionTask>> taskPage(Integer pageNum, Integer pageSize, String status);

    Result<InspectionTask> getTaskById(Long id);

    Result<List<InspectionResult>> getTaskResults(Long taskId, Long hostId);

    Result<List<InspectionResult>> getHostResults(Long taskId, Long hostId);
}
