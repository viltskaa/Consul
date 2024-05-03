package com.example.consul.document.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Setter
@Data
@AllArgsConstructor
public class HeaderConfig {
    private String title;
    private String description;
}
