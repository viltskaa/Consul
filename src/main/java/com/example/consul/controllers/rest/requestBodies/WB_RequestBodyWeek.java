package com.example.consul.controllers.rest.requestBodies;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WB_RequestBodyWeek {
    private String apiKey;
    private Integer year;
    private Integer month;
    private Integer weekNumber;
}
