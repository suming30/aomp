package com.lsm.aomp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TempAuthCreateDTO {

    private Long userId;

    private java.util.List<Long> roleIds;

    private java.util.List<Long> resourceIds;

    @NotBlank(message = "授权原因不能为空")
    private String reason;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
