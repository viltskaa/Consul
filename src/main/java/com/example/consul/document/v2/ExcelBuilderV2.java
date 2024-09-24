package com.example.consul.document.v2;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.annotations.TotalCell;
import com.example.consul.document.models.ReportFile;
import com.example.consul.document.v1.configurations.ExcelCellType;
import com.example.consul.document.v2.models.CellStyleValues;
import com.example.consul.document.v2.models.CellWithParams;
import com.example.consul.document.v2.models.Sheet;
import com.example.consul.document.v2.models.Table;
import com.example.consul.document.v2.utils.ObjectDeepReflection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class ExcelBuilderV2<T> {
    private String headerTitle;
    private String headerSubtitle;
    private List<Sheet<T>> sheets;
    private String filename;

    private Map<ExcelCellType, CellStyle> cellStyles;

    private String numbersToCellAddress(int row, int column) {
        return CellReference.convertNumToColString(column) + row;
    }

    public static <T> ExcelBuilderV2<T>.Builder builder() {
        return new ExcelBuilderV2<T>().new Builder();
    }

    private void createTable(
            @NotNull org.apache.poi.ss.usermodel.Sheet sheet,
            @NotNull Table<T> table,
            @NotNull Integer startIndex
    ) {
        List<String> tableHeader = table.getHeader();
        if (tableHeader == null)
            return;

        Row headerRow = table.getName() != null && !table.getName().isEmpty()
                ? sheet.createRow(startIndex++)
                : null;

        Row row = sheet.createRow(startIndex++);

        CellStyle boldStyle = sheet.getWorkbook().createCellStyle();
        boldStyle.cloneStyleFrom(cellStyles.get(ExcelCellType.BASE));
        Font boldFont = sheet.getWorkbook().createFont();
        boldFont.setFontHeightInPoints((short) 16);
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        boldStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        boldStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < tableHeader.size(); i++) {
            sheet.autoSizeColumn(i);

            Cell cell = row.createCell(i);
            cell.setCellStyle(boldStyle);
            cell.setCellValue(tableHeader.get(i));

            if (headerRow != null) {
                headerRow.createCell(i);
            }
        }

        if (headerRow != null) {
            Cell cell = headerRow.getCell(0);
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.cloneStyleFrom(cellStyles.get(ExcelCellType.BASE));

            Font font = sheet.getWorkbook().createFont();
            font.setFontHeightInPoints((short) 24);
            style.setFont(font);
            cell.setCellStyle(style);
            cell.setCellValue(table.getName());
            CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
            CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);

            sheet.addMergedRegion(
                    new CellRangeAddress(
                            headerRow.getRowNum(),
                            headerRow.getRowNum(),
                            0,
                            headerRow.getLastCellNum() - 1)
            );
        }

        for (int rowIndex = 0; rowIndex < table.getDataSize(); rowIndex++) {
            Row tableRow = sheet.createRow(startIndex++);
            Object obj = table.getData().get(rowIndex);
            if (obj == null) {
                return;
            }

            String formula = obj.getClass().getAnnotation(TotalCell.class).formula();

            List<CellWithParams> values = ObjectDeepReflection.getCells(obj);
            Collections.reverse(values);

            int column = 0;
            for (String head : tableHeader) {
                CellWithParams cellWithParams = values.stream()
                        .filter(x -> x.getName().equals(head))
                        .findFirst().orElse(null);
                if (cellWithParams == null) {
                    continue;
                }

                Cell cell = tableRow.createCell(column);
                cell.setCellStyle(cellStyles.get(cellWithParams.getType()));
                sheet.autoSizeColumn(column);

                if (formula != null) {
                    formula = formula.replace(
                            cellWithParams.getFieldName(),
                            numbersToCellAddress(startIndex, column)
                    );
                }

                if (cellWithParams.isTotal()) {
                    cell.setCellFormula(formula);
                } else if (cellWithParams.getValue() == null
                        || cellWithParams.getValue().equals(0)
                        || cellWithParams.getValue().equals(0.00)) {
                    cell.setCellValue("");
                } else if (cellWithParams.getValue().getClass().equals(String.class)) {
                    cell.setCellValue(cellWithParams.getValue().toString());
                } else if (cellWithParams.getValue().getClass().equals(Integer.class)) {
                    cell.setCellValue((Integer) cellWithParams.getValue());
                } else if (cellWithParams.getValue().getClass().equals(Double.class)) {
                    cell.setCellValue((Double) cellWithParams.getValue());
                } else {
                    cell.setCellValue(cellWithParams.getDefaultValue());
                }
                column++;
            }
        }
        Row totalRow = sheet.createRow(startIndex);
        List<Field> fields = ObjectDeepReflection.getFieldsWithAnnotation(
                table.getDataClass(),
                CellUnit.class
        );
        int column = 0;

        for (Field field : fields) {
            Cell cell = totalRow.createCell(column);
            cell.setCellStyle(cellStyles.get(ExcelCellType.TOTAL));
            sheet.autoSizeColumn(column);

            if (!field.getType().equals(String.class)) {
                cell.setCellFormula(
                        "SUM(%s:%s)".formatted(
                                numbersToCellAddress(totalRow.getRowNum() - table.getDataSize() + 1, column),
                                numbersToCellAddress(totalRow.getRowNum(), column)
                        )
                );
            }

            column++;
        }
    }

    private void createSheet(@NotNull Workbook workbook, @NotNull Sheet<T> sheet) {
        org.apache.poi.ss.usermodel.Sheet excelSheet = workbook.createSheet(sheet.getName());
        int offset = 0;
        for (Table<T> table : sheet.getTables()) {
            createTable(excelSheet, table, offset);
            offset += table.getHeight();
        }
    }

    private byte[] createWorkbook() {
        try (Workbook workbook = new XSSFWorkbook()) {
            ExcelBuilderV2.this.cellStyles = new HashMap<>();

            ExcelBuilderV2.this.cellStyles.put(
                    ExcelCellType.BASE, CellStyleValues.BASE.getCellStyle(workbook)
            );
            ExcelBuilderV2.this.cellStyles.put(
                    ExcelCellType.EXPENSIVE, CellStyleValues.EXPENSE.getCellStyle(workbook)
            );
            ExcelBuilderV2.this.cellStyles.put(
                    ExcelCellType.TOTAL, CellStyleValues.TOTAL.getCellStyle(workbook)
            );

            for (Sheet<T> sheet : sheets) {
                createSheet(workbook, sheet);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            byteArrayOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException exception) {
            return null;
        }
    }

    public ReportFile createDocument() {
        byte[] byteArrayReport = createWorkbook();
        if (byteArrayReport == null) {
            return null;
        }

        ByteArrayResource report = new ByteArrayResource(byteArrayReport);

        return new ReportFile(filename, report);
    }

    public class Builder {
        public Builder setHeaderTitle(String headerTitle) {
            ExcelBuilderV2.this.headerTitle = headerTitle;
            return this;
        }

        public Builder setHeaderSubtitle(String headerSubtitle) {
            ExcelBuilderV2.this.headerSubtitle = headerSubtitle;
            return this;
        }

        public Builder setFilename(String filename) {
            ExcelBuilderV2.this.filename = filename;
            return this;
        }

        @SafeVarargs
        public final Builder setSheets(Sheet<T>... sheets) {
            ExcelBuilderV2.this.sheets = Arrays.stream(sheets).toList();
            return this;
        }

        public Builder setSheets(List<Sheet<T>> sheets) {
            ExcelBuilderV2.this.sheets = sheets;
            return this;
        }

        public ExcelBuilderV2<T> build() {
            return ExcelBuilderV2.this;
        }
    }
}
