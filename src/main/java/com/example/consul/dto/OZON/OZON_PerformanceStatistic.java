package com.example.consul.dto.OZON;

import lombok.Data;

/**
 * Создание отчета по трафаретам. Необходимо его скачать по UUID
 */
@Data
public class OZON_PerformanceStatistic {
    private String UUID;
    private String vendor;
}
