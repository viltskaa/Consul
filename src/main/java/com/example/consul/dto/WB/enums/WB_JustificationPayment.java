package com.example.consul.dto.WB.enums;

public enum WB_JustificationPayment {
    RETURN("Возврат"),
    SALE("Продажа"),
    SALES_ADJUSTMENT("Коррекция продаж"),
    COMPENSATION_DAMAGE("Компенсация ущерба"),
    VOLUNTARY_COMPENSATION_RETURN("Добровольная компенсация при возврате"),
    ACQUIRING_ADJUSTMENT("Корректировка эквайринга"),
    LOGISTIC("Логистика"),
    LOGISTICS_ADJUSTMENT("Коррекция логистики"),
    STORAGE("Хранение"),
    PENALTY("Штраф"),
    DEDUCTION("Удержание"),
    LOGISTIC_STORNO("Логистика сторно");

    private final String value;

    WB_JustificationPayment(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
