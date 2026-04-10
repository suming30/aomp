package com.lsm.aomp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditLevel {
    NORMAL("normal"),
    IMPORTANT("important"),
    CRITICAL("critical");

    private final String value;
}
