package com.lsm.aomp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExecuteMode {
    CONCURRENT("concurrent"),
    SERIAL("serial"),
    BATCH("batch");

    private final String value;
}
