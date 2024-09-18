package com.example.consul.document.v1.configurations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExcelCellConfig {
    private String name;
    private ExcelCellType type;
    private Boolean expandStyle;
    private Integer columnId;
}
