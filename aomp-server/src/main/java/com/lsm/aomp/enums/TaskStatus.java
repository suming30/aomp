package com.lsm.aomp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus {
    PENDING("pending"),
    RUNNING("running"),
    PAUSED("paused"),
    COMPLETED("completed"),
    FAILED("failed"),
    TERMINATED("terminated"),
    APPROVING("approving");

    private final String value;
}
