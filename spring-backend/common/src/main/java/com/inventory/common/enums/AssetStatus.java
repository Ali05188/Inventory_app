package com.inventory.common.enums;

public enum AssetStatus {
    PENDING("pending"),
    IN_SERVICE("in_service"),
    RETIRED("retired"),
    TRANSFERRED("transferred"),
    DISPOSED("disposed");

    private final String value;

    AssetStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AssetStatus fromValue(String value) {
        for (AssetStatus status : AssetStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}

