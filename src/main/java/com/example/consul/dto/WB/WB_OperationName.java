package com.example.consul.dto.WB;

public enum WB_OperationName {
    RETURN("Возврат"),
    SALE("Продажа"),
    LOGISTIC("Логистика"),
    REFUND("Возмещение издержек по перевозке/по складским операциям с товаром"),
    STORAGE("Хранение"),
    STORAGE_REFUND("Пересчет хранения"),
    HOLDING("Удержание"),
    PENALTY("Штраф"),
    DEDUCTION("Удержание"),
    COMPENSATION_REPlACED("Компенсация подмененного товара"),
    COMPENSATION_LOSTED("Компенсация потерянного товара"),
    COMPENSATION_DEFECT("Компенсация брака"),
    SALE_TYPE("Клиентский");

    private final String value;

    WB_OperationName(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
