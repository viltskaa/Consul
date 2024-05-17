package com.example.consul.dto.OZON;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OZON_PerformanceTokenExpires {
    private String access_token;
    private Long expires_in;
}
