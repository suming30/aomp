package com.lsm.aomp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScriptScope {
    PRIVATE("private"),
    TEAM("team"),
    PUBLIC("public");

    private final String value;
}
