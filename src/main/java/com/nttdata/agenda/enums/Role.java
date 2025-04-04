package com.nttdata.agenda.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    USER,
    ADMIN;

    @JsonCreator
    public static Role fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El valor no puede ser nulo o vacío.");
        }
        return Role.valueOf(value.trim().toUpperCase());
    }
}
