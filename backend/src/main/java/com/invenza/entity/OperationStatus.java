package com.invenza.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OperationStatus {
    DRAFT("draft"),
    WAITING("waiting"),
    READY("ready"),
    DONE("done"),
    CANCELED("canceled");

    private final String value;

    OperationStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OperationStatus fromValue(String value) {
        if (value == null) return null;
        for (OperationStatus status : values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown OperationStatus: " + value);
    }
}

