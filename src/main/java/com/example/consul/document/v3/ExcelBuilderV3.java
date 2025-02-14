package com.example.consul.document.v3;

import com.example.consul.document.models.ReportFile;
import com.example.consul.document.v1.configurations.ExcelCellType;
import com.example.consul.document.v2.models.CellStyleValues;
import com.example.consul.document.v2.models.CellWithParams;
import com.example.consul.document.v2.models.Sheet;
import com.example.consul.document.v2.models.Table;
import com.example.consul.document.v2.utils.ObjectDeepReflection;
import com.example.consul.utils.enumerate.Enumerate;
import com.example.consul.utils.enumerate.Pair;
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

public class ExcelBuilderV3<T> {
    private String headerTitle;
    private String headerSubtitle;
    private List<Sheet<T>> sheets;
    private String filename;

    private Map<ExcelCellType, CellStyle> cellStyles;

    private String numbersToCellAddress(int row, int column) {
        return CellReference.convertNumToColString(column) + row;
    }

    public static <T> ExcelBuilderV3<T>.Builder builder() {
        return new ExcelBuilderV3<T>().new Builder();
    }

    private Integer createTable(
            @NotNull org.apache.poi.ss.usermodel.Sheet sheet,
            @NotNull Table<T> table,
            @NotNull Integer startIndex
    ) {
        List<String> tableMainHeader = table.getMainHeader();
        if (tableMainHeader == null)
            return null;

        Row headerRow = table.getName() != null && !table.getName().isEmpty()
                ? sheet.createRow(startIndex++)
                : null;

        Row row = sheet.createRow(startIndex++);

        CellStyle boldStyle = sheet.getWorkbook().createCellStyle();
        boldStyle.cloneStyleFrom(cellStyles.get(ExcelCellType.BASE));
        Font boldFont = sheet.getWorkbook().createFont();
        boldFont.setFontHeightInPoints((short) 12);
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        boldStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        boldStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < tableMainHeader.size(); i++) {
            sheet.autoSizeColumn(i);

            Cell cell = row.createCell(i);
            cell.setCellStyle(boldStyle);
            cell.setCellValue(tableMainHeader.get(i));

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
                return null;
            }

            List<CellWithParams> values = ObjectDeepReflection.getMainCells(obj);
            Collections.reverse(values);

            for (Pair<String> head : Enumerate.of(tableMainHeader)) {
                CellWithParams cellWithParams = values.stream()
                        .filter(x -> x.getName().equals(head.getValue()))
                        .findFirst().orElse(null);
                if (cellWithParams == null) {
                    continue;
                }

                int column = head.getIndex();

                Cell cell = tableRow.createCell(column);
                cell.setCellStyle(cellStyles.get(cellWithParams.getType()));
                sheet.autoSizeColumn(column);

                if (cellWithParams.getValue() == null
                        || cellWithParams.getValue().equals(0)
                        || cellWithParams.getValue().equals(0.00)) {
                    cell.setCellValue(cellWithParams.getDefaultValue());
                } else if (cellWithParams.getValue().getClass().equals(String.class)) {
                    cell.setCellValue(cellWithParams.getValue().toString());
                } else if (cellWithParams.getValue().getClass().equals(Integer.class)) {
                    cell.setCellValue((Integer) cellWithParams.getValue());
                } else if (cellWithParams.getValue().getClass().equals(Double.class)) {
                    cell.setCellValue((Double) cellWithParams.getValue());
                }
            }
        }
        Row totalRow = sheet.createRow(startIndex);
        List<Field> fields = ObjectDeepReflection.getMainFieldsWithCellUnit(
                table.getDataClass()
        );

        int column = 0;

        for (Pair<Field> field : Enumerate.of(fields)) {
            column = field.getIndex();
            Cell cell = totalRow.createCell(column);
            cell.setCellStyle(cellStyles.get(ExcelCellType.TOTAL));
            sheet.autoSizeColumn(column);

            if (!field.getValue().getType().equals(String.class)) {
                cell.setCellFormula(
                        "SUM(%s:%s)".formatted(
                                numbersToCellAddress(totalRow.getRowNum() - table.getDataSize() + 1, column),
                                numbersToCellAddress(totalRow.getRowNum(), column)
                        )
                );
            }
        }

        Cell cell = totalRow.createCell(totalRow.getLastCellNum());
        Row tempRow = sheet.getRow(startIndex - 1);

        Cell nameCell = tempRow.createCell(totalRow.getLastCellNum() - 1);
        Cell nameCell2 = totalRow.createCell(totalRow.getRowNum() - table.getDataSize()-2);

        nameCell2.setCellValue("ИТОГО:");
        nameCell2.setCellStyle(boldStyle);

        nameCell.setCellValue("ИТОГО:");
        nameCell.setCellStyle(boldStyle);

        cell.setCellFormula(
                "%s-%s".formatted(
                        numbersToCellAddress(totalRow.getRowNum() + 1, column - 2),
                        numbersToCellAddress(totalRow.getRowNum() + 1, column)
                )
        );
        cell.setCellStyle(cellStyles.get(ExcelCellType.TOTAL));
        startIndex +=2;

        List<String> tableSubHeader = table.getSubHeader();
        if (tableSubHeader == null)
            return null;

        Row headerRow1 = table.getName() != null && !table.getName().isEmpty()
                ? sheet.createRow(startIndex++)
                : null;

        Row row1 = sheet.createRow(startIndex++);

        for (int i = 0; i < tableSubHeader.size(); i++) {
            sheet.autoSizeColumn(i);

            Cell tempCell = row1.createCell(i);
            tempCell.setCellStyle(boldStyle);
            tempCell.setCellValue(tableSubHeader.get(i));

            if (headerRow1 != null) {
                headerRow1.createCell(i);
            }
        }

        List<Double> columnSums = new ArrayList<>(Collections.nCopies(tableSubHeader.size(), 0.0));

        for (int rowIndex = 0; rowIndex < table.getDataSize(); rowIndex++) {
            Object obj = table.getData().get(rowIndex);
            if (obj == null) {
                return null;
            }

            List<CellWithParams> values = ObjectDeepReflection.getSubCells(obj);
            Collections.reverse(values);

            for (Pair<String> head : Enumerate.of(tableSubHeader)) {
                CellWithParams cellWithParams = values.stream()
                        .filter(x -> x.getName().equals(head.getValue()))
                        .findFirst().orElse(null);
                if (cellWithParams == null || cellWithParams.getValue() == null) {
                    continue;
                }

                int column1 = head.getIndex();

                if (cellWithParams.getValue() instanceof Number) {
                    columnSums.set(column1, columnSums.get(column1) + ((Number) cellWithParams.getValue()).doubleValue());
                }
            }
        }

        Row totalRow1 = sheet.createRow(startIndex++);
        for (int i = 0; i < columnSums.size(); i++) {
            Cell cell2 = totalRow1.createCell(i);
            cell2.setCellStyle(cellStyles.get(ExcelCellType.TOTAL));
            cell2.setCellValue(columnSums.get(i));
            sheet.autoSizeColumn(i);
        }

        Cell formulaCell = totalRow1.createCell(columnSums.size());
        Row tempRow1 = sheet.getRow(startIndex - 2);

        Cell nameCell1 = tempRow1.createCell(totalRow1.getLastCellNum() - 1);
        nameCell1.setCellValue("Итого комиссия маркетплейса:");
        nameCell1.setCellStyle(boldStyle);
        formulaCell.setCellStyle(cellStyles.get(ExcelCellType.TOTAL));
        String formula =
                "(IF(OR(%s=\"\", %s=0), 0, %s) - IF(OR(%s=\"\", %s=0), 0, %s)".formatted(
                        numbersToCellAddress(totalRow.getRowNum() + 5, column - 4),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column - 4),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column - 4),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column - 2),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column - 2),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column - 2)) +
                " + IF(OR(%s=\"\", %s=0), 0, %s) - IF(OR(%s=\"\", %s=0), 0, %s)".formatted(
                        numbersToCellAddress(totalRow.getRowNum() + 5, column - 1),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column - 1),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column - 1),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column)) +
                " - IF(OR(%s=\"\", %s=0), 0, %s) - IF(OR(%s=\"\", %s=0), 0, %s)".formatted(
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 1),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 1),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 1),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 2),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 2),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 2)) +
                " - IF(OR(%s=\"\", %s=0), 0, %s) - IF(OR(%s=\"\", %s=0), 0, %s))*(-1)".formatted(
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 3),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 3),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 3),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 4),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 4),
                        numbersToCellAddress(totalRow.getRowNum() + 5, column + 4));
        formulaCell.setCellFormula(formula);
        sheet.autoSizeColumn(columnSums.size());

        Row tempRow2 = sheet.createRow(startIndex+2);
        nameCell1 = tempRow2.createCell(totalRow1.getLastCellNum() - 1);
        nameCell1.setCellValue("Всего к начислению:");
        nameCell1.setCellStyle(boldStyle);

        Row tempRow3 = sheet.createRow(startIndex+3);
        formulaCell = tempRow3.createCell(columnSums.size());
        formulaCell.setCellStyle(cellStyles.get(ExcelCellType.TOTAL));
        formula = "IF(OR(%s=\"\", %s=0), 0, %s) - IF(OR(%s=\"\", %s=0), 0, %s) - IF(OR(%s=\"\", %s=0), 0, %s)".formatted(
                        numbersToCellAddress(totalRow.getRowNum()+1, column - 2),
                        numbersToCellAddress(totalRow.getRowNum()+1, column - 2),
                        numbersToCellAddress(totalRow.getRowNum()+1, column - 2),
                        numbersToCellAddress(totalRow.getRowNum()+1, column),
                        numbersToCellAddress(totalRow.getRowNum()+1, column),
                        numbersToCellAddress(totalRow.getRowNum()+1, column),
                        numbersToCellAddress(totalRow1.getRowNum()+1, column+5),
                        numbersToCellAddress(totalRow1.getRowNum()+1, column+5),
                        numbersToCellAddress(totalRow1.getRowNum()+1, column+5));

        formulaCell.setCellFormula(formula);
        sheet.autoSizeColumn(columnSums.size());

        return startIndex + 6;
    }

    private void createSheet(@NotNull Workbook workbook, @NotNull Sheet<T> sheet) {
        org.apache.poi.ss.usermodel.Sheet excelSheet = workbook.createSheet(sheet.getName());
        int offset = 0;
        for (Table<T> table : sheet.getTables()) {
            Integer temp = createTable(excelSheet, table, offset);
            if (temp != null) {
                offset = temp;
            }
        }
    }

    private byte[] createWorkbook() {
        try (Workbook workbook = new XSSFWorkbook()) {
            ExcelBuilderV3.this.cellStyles = new HashMap<>();

            ExcelBuilderV3.this.cellStyles.put(
                    ExcelCellType.BASE, CellStyleValues.BASE.getCellStyle(workbook)
            );
            ExcelBuilderV3.this.cellStyles.put(
                    ExcelCellType.EXPENSIVE, CellStyleValues.EXPENSE.getCellStyle(workbook)
            );
            ExcelBuilderV3.this.cellStyles.put(
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
            ExcelBuilderV3.this.headerTitle = headerTitle;
            return this;
        }

        public Builder setHeaderSubtitle(String headerSubtitle) {
            ExcelBuilderV3.this.headerSubtitle = headerSubtitle;
            return this;
        }

        public Builder setFilename(String filename) {
            ExcelBuilderV3.this.filename = filename;
            return this;
        }

        @SafeVarargs
        public final Builder setSheets(Sheet<T>... sheets) {
            ExcelBuilderV3.this.sheets = Arrays.stream(sheets).toList();
            return this;
        }

        public Builder setSheets(List<Sheet<T>> sheets) {
            ExcelBuilderV3.this.sheets = sheets;
            return this;
        }

        public ExcelBuilderV3<T> build() {
            return ExcelBuilderV3.this;
        }
    }
}
