package com.invenza.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OperationType {
    IN("IN"),
    OUT("OUT"),
    TRANSFER("TRANSFER"),
    ADJUSTMENT("ADJUSTMENT");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OperationType fromValue(String value) {
        if (value == null) return null;
        for (OperationType type : values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown OperationType: " + value);
    }
}

