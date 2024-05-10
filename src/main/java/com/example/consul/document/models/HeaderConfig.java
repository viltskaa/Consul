package com.example.consul.document.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HeaderConfig {
    private String title;
    private String description;
}
