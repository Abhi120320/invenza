package com.invenza.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MoveDirection {
    IN("IN"),
    OUT("OUT");

    private final String value;

    MoveDirection(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MoveDirection fromValue(String value) {
        if (value == null) return null;
        for (MoveDirection dir : values()) {
            if (dir.value.equalsIgnoreCase(value) || dir.name().equalsIgnoreCase(value)) {
                return dir;
            }
        }
        throw new IllegalArgumentException("Unknown MoveDirection: " + value);
    }
}

