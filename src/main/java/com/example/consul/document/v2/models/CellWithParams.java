package com.example.consul.document.v2.models;

import com.example.consul.document.v1.configurations.ExcelCellType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CellWithParams {
    private String name;
    private String fieldName;
    private int width;
    private boolean total;
    private ExcelCellType type;
    private String defaultValue;
    private Object value;
}
