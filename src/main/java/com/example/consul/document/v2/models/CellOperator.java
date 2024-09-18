package com.example.consul.document.v2.models;

import jakarta.validation.constraints.NotNull;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public interface CellOperator {
    @NotNull
    CellStyle getCellStyle(@NotNull Workbook workbook);
}
