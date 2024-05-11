package com.example.consul.dto.WB;

public enum WB_OperationName {
    RETURN("Возврат"),
    SALE("Продажа"),
    LOGISTIC("Логистика"),
    REFUND("Возмещение издержек по перевозке/по складским операциям с товаром"),
    STORAGE("Хранение"),
    HOLDING("Удержание"),
    PENALTY("Штраф");

    private final String value;

    WB_OperationName(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
