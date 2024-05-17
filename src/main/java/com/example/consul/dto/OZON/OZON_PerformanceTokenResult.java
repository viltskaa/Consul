package com.example.consul.dto.OZON;

import lombok.Data;

@Data
public class OZON_PerformanceTokenResult {
    private String access_token;
    private Integer expires_in;
    private String token_type;
}
