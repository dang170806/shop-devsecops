package com.devon.building.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_MANAGER("Manager"),
    ROLE_STAFF("Staff"),
    ROLE_USER("User");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getCode() {
        return this.name(); // ROLE_MANAGER
    }

    public String getLabel() {
        return label;       // Manager
    }

}
