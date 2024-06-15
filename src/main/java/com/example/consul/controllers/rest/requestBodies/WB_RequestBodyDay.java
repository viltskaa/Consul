package com.example.consul.controllers.rest.requestBodies;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WB_RequestBodyDay {
    private String apiKey;
    private String day;
}
