package com.example.consul.document.configurations;

import java.util.Arrays;

public enum ExcelCellType {
    BASE,
    EXPENSIVE,
    TOTAL;

    public static Integer getIndex(ExcelCellType type) {
        for (int i = 0; i < ExcelCellType.values().length; i++) {
            if (ExcelCellType.values()[i] == type) return i;
        }
        return null;
    }
}
