package com.lsm.aomp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HostStatus {
    ONLINE("online"),
    OFFLINE("offline"),
    DELETED("deleted");

    private final String value;
}
