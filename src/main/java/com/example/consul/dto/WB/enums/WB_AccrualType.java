package com.example.consul.dto.WB.enums;

public enum WB_AccrualType {
    RETURN("Возврат"),
    SALE("Продажа");

    private final String value;

    WB_AccrualType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
