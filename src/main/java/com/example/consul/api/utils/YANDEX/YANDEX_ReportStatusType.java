package com.example.consul.api.utils.YANDEX;

public enum YANDEX_ReportStatusType {
    PENDING, // отчет ожидает генерации
    PROCESSING, //  отчет генерируется
    FAILED, //во время генерации произошла ошибка
    DONE // отчет готов
}
