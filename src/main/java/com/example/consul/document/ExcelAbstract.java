package com.example.consul.document;

import com.example.consul.document.Annotations.CellUnit;
import com.example.consul.document.configurations.ExcelCellType;
import com.example.consul.document.configurations.ExcelConfig;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.apache.poi.ss.usermodel.Font.COLOR_RED;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;

public abstract class ExcelAbstract {
    private List<CellStyle> cellStyles;

    @NotNull
    protected CellStyle createBaseStyle(@NotNull Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont font = (HSSFFont) workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    @NotNull
    protected CellStyle createExpenseStyle(@NotNull Workbook workbook) {
        CellStyle styleExpense = workbook.createCellStyle();
        styleExpense.setWrapText(true);
        styleExpense.setAlignment(CENTER);
        styleExpense.setVerticalAlignment(VerticalAlignment.CENTER);
        styleExpense.setBorderBottom(BorderStyle.THIN);
        styleExpense.setBorderLeft(BorderStyle.THIN);
        styleExpense.setBorderTop(BorderStyle.THIN);
        styleExpense.setBorderRight(BorderStyle.THIN);
        HSSFFont fontExpense = (HSSFFont) workbook.createFont();
        fontExpense.setFontName("Calibri");
        fontExpense.setFontHeightInPoints((short) 11);
        fontExpense.setColor(COLOR_RED);
        styleExpense.setFont(fontExpense);
        return styleExpense;
    }

    private void addHeader(@NotNull Workbook workbook) {

    }

    public void setTableTitle(@NotNull CellStyle style,
                              @NotNull Row header,
                              @NotNull Sheet sheet,
                              int columnInd,
                              String titleName) {
        Cell cell = header.createCell(columnInd);
        cell.setCellValue(titleName);
        cell.setCellStyle(style);
        sheet.autoSizeColumn(columnInd);
//        sheet.setColumnWidth(columnInd, 15 * 256);
    }

    private void addPageHeader(@NotNull Workbook workbook,
                                   @NotNull Sheet sheet,
                                   @NotNull List<Field> fields) {
        int columnIndex = 0;

        Row header = sheet.createRow(0);

        for (Field field : fields) {
            if (field.isAnnotationPresent(CellUnit.class)) {
                CellUnit cellUnit = field.getAnnotation(CellUnit.class);
                setTableTitle(cellUnit.type().equals(ExcelCellType.BASE)
                                ? cellStyles.get(0)
                                : cellStyles.get(1),
                        header,
                        sheet,
                        columnIndex,
                        cellUnit.name());
            }
        }
    }

    public <T> void createDocument(@NotNull ExcelConfig<T> config) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(config.getSheetName());
        cellStyles = List.of(createBaseStyle(workbook), createExpenseStyle(workbook));

        List<Field> fields = Arrays.stream(config.getDataClass().getFields()).toList();
        List<T> data = config.getData();
        addPageHeader(workbook, sheet, fields);

        for (int rowIndex = 0; rowIndex < config.getData().size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex);
            int columnInd = 0;

            for (Field field : fields) {
                if (field.isAnnotationPresent(CellUnit.class)) {
                    CellUnit cellUnit = field.getAnnotation(CellUnit.class);
                    Cell cell = row.createCell(columnInd++);
                    cell.setCellStyle(cellUnit.type().equals(ExcelCellType.BASE)
                            ? cellStyles.get(0)
                            : cellStyles.get(1));
                    String value = "";
                    try {
                        if (field.get(data.get(rowIndex)) == null) {
                            value = cellUnit.defaultValue();
                        }
                        else {
                            if (cellUnit.inverse() && (field.getType() == Integer.class || field.getType() == Double.class)) {
                                switch (field.getType().getSimpleName()) {
                                    case "Integer" -> value = String.valueOf((Integer)field.get(data.get(rowIndex)) * -1);
                                    case "Double" -> value = String.valueOf((Double)field.get(data.get(rowIndex)) * -1);
                                }
                            } else {
                                value = String.valueOf(field.get(data.get(rowIndex)));
                            }
                        }
                    }
                    catch (IllegalAccessException e) {
                        value = "";
                    }

                    cell.setCellValue(value);
                }
            }
        }

        workbook.write(new FileOutputStream(config.getFileName()));
        workbook.close();
    }
}
