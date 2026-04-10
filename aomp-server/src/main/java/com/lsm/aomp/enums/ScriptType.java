package com.lsm.aomp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScriptType {
    SHELL("shell"),
    PYTHON("python"),
    POWERSHELL("powershell");

    private final String value;
}
