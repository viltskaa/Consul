package com.example.consul.controllers.rest.requestBodies;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OZON_RequestBody {
    private String apiKey;
    private String clientId;
    private String performanceClientId;
    private String performanceClientSecret;
    private Integer year;
    private Integer month;
}
