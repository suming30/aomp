package com.lsm.aomp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    ENABLED("enabled"),
    DISABLED("disabled"),
    LOCKED("locked");

    private final String value;
}
