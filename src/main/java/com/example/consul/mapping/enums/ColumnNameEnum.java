package com.example.consul.mapping.enums;

public enum ColumnNameEnum {
    DEFAULT(""),
    SKU("Ваш SKU"),
    DELIVERED_COUNT("Доставлено, шт."),
    TOTAL_DISCOUNT("Сумма всех скидок для доставленных штук, руб."),
    TOTAL_PRICE_WITH_DISCOUNT("Стоимость всех доставленных штук с НДС с учётом всех скидок, руб."),
    ORDER_NUMBER("Номер заказа"),
    RETURN_COUNT("Возвращено, шт."),
    PRICE_WITH_DISCOUNT("Цена с НДС с учётом всех скидок, руб. за шт."),
    SERVICE_COST("Стоимость услуги, ₽"),
    POST_PAYMENT("Постоплата, ₽"),
    SERVICE_COST_PLACEMENT("Стоимость услуги (гр.48=гр. 35-гр.37+гр.42+гр.44-гр.45-гр.46-гр.47), ₽"),
    TARIFF("Тариф за заказ или отправление, ₽");

    public final String name;

    ColumnNameEnum(String name) {
        this.name = name;
    }
}
