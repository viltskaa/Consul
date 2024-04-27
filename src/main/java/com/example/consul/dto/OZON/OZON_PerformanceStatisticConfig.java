package com.example.consul.dto.OZON;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
@Data
public class OZON_PerformanceStatisticConfig {
    private List<String> campaigns;
    private String dateFrom;
    private String dateTo;
    private GroupBy groupBy;

    public enum GroupBy {
        START_OF_MONTH
    }
}
