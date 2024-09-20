package com.example.consul.document.v2.models;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.jetbrains.annotations.NotNull;

import static org.apache.poi.ss.usermodel.Font.COLOR_RED;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;

public enum CellStyleValues implements CellOperator {
    BASE {
        @Override
        public @NotNull CellStyle getCellStyle(@NotNull Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            style.setAlignment(CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setFontName("Calibri");
            font.setFontHeightInPoints((short) 11);
            style.setFont(font);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            return style;
        }
    },
    EXPENSE {
        @Override
        public @NotNull CellStyle getCellStyle(@NotNull Workbook workbook) {
            CellStyle style = CellStyleValues.BASE.getCellStyle(workbook);
            XSSFFont fontExpense = (XSSFFont) workbook.createFont();
            fontExpense.setColor(COLOR_RED);
            style.setFont(fontExpense);
            return style;
        }
    },
    TOTAL {
        @Override
        public @NotNull CellStyle getCellStyle(@NotNull Workbook workbook) {
            CellStyle style = CellStyleValues.BASE.getCellStyle(workbook);
            style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
            return style;
        }
    },


}
