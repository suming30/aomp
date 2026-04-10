package com.lsm.aomp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TaskCreateDTO {

    @NotBlank(message = "任务名称不能为空")
    @Size(max = 200, message = "任务名称不超过200字符")
    private String taskName;

    private Long scriptId;

    private Integer scriptVersion;

    @NotBlank(message = "执行方式不能为空")
    private String executeMode;

    private Integer batchSize;

    private Integer batchInterval;

    private String taskParams;

    private Integer timeout = 3600;

    private Boolean isDryRun = false;

    private Boolean isSilent = false;

    private List<Long> hostIds;

    private List<Long> hostGroupIds;
}
