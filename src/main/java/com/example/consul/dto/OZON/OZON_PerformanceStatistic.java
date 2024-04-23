package com.example.consul.dto.OZON;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Создание отчета по трафаретам. Необходимо его скачать по UUID
 */
@Getter
@Setter
@Data
public class OZON_PerformanceStatistic {
    private String UUID;
    private String vendor;
}
