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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.poi.ss.usermodel.Font.COLOR_RED;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;

public class ExcelBuilder {
    private static List<CellStyle> cellStyles;

    private ExcelBuilder() {}

    @NotNull
    private static CellStyle createBaseStyle(@NotNull Workbook workbook) {
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
    private static CellStyle createExpenseStyle(@NotNull Workbook workbook) {
        CellStyle style = createBaseStyle(workbook);
        HSSFFont fontExpense = (HSSFFont) workbook.createFont();
        fontExpense.setFontName("Calibri");
        fontExpense.setFontHeightInPoints((short) 11);
        fontExpense.setColor(COLOR_RED);
        style.setFont(fontExpense);
        return style;
    }

    @NotNull
    private static CellStyle createTotalStyle(@NotNull Workbook workbook) {
        CellStyle style = createBaseStyle(workbook);
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        HSSFFont fontExpense = (HSSFFont) workbook.createFont();
        fontExpense.setFontName("Calibri");
        fontExpense.setFontHeightInPoints((short) 11);
        style.setFont(fontExpense);
        return style;
    }

    private static void addHeader(@NotNull Workbook workbook) {

    }

    private static void setTableTitle(@NotNull CellStyle style,
                              @NotNull Row header,
                              @NotNull Sheet sheet,
                              int columnInd,
                              String titleName) {
        Cell cell = header.createCell(columnInd);
        cell.setCellValue(titleName);
        cell.setCellStyle(style);
//        sheet.autoSizeColumn(columnInd);
        sheet.setColumnWidth(columnInd, 15 * 256);
    }

    private static void addPageHeader(@NotNull Sheet sheet,
                                      @NotNull List<Field> fields) {
        int columnIndex = 0;

        Row header = sheet.createRow(0);

        for (Field field : fields) {
            if (field.isAnnotationPresent(CellUnit.class)) {
                CellUnit cellUnit = field.getAnnotation(CellUnit.class);
                Integer index = ExcelCellType.getIndex(cellUnit.type());
                setTableTitle(cellStyles.get(index != null ? index : 0),
                        header,
                        sheet,
                        columnIndex++,
                        cellUnit.name());
            }
        }
    }

    public static <T> void createDocument(@NotNull ExcelConfig<T> config) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(config.getSheetName());
        cellStyles = List.of(
                createBaseStyle(workbook),
                createExpenseStyle(workbook),
                createTotalStyle(workbook));

        List<Field> fields = Arrays.stream(config.getDataClass().getDeclaredFields()).toList();
        List<Method> methods = Arrays.stream(config.getDataClass().getDeclaredMethods())
                .filter(x -> !x.getAnnotatedReturnType().getType().equals(void.class)).toList();
        List<T> data = config.getData();
        List<Double> sumTotal = new ArrayList<>();
        addPageHeader(sheet, fields);
        int rowIndex;
        for (rowIndex = 0; rowIndex < config.getData().size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1);
            int columnInd = 0;

            for (Field field : fields) {
                if (rowIndex == 0) {
                    sumTotal.add(0.0);
                }
                if (field.isAnnotationPresent(CellUnit.class)) {
                    CellUnit cellUnit = field.getAnnotation(CellUnit.class);
                    Cell cell = row.createCell(columnInd++);

                    Integer index = ExcelCellType.getIndex(cellUnit.type());
                    cell.setCellStyle(cellStyles.get(index != null ? index : 0));

                    try {
                        Method method = methods.stream()
                                .filter(x -> x.getName().toLowerCase().contains(field.getName().toLowerCase()))
                                .findFirst().orElse(null);
                        if (method == null) {
                            cell.setCellValue(cellUnit.defaultValue());
                        }
                        else {
                            Object returnFromMethod = method.invoke(data.get(rowIndex));
                            if (returnFromMethod == null) {
                                cell.setCellValue(cellUnit.defaultValue());
                            }
                            else {
                                if (field.getType().equals(String.class)) {
                                    cell.setCellValue(returnFromMethod.toString());
                                }
                                else {
                                    if (cellUnit.inverse()
                                            && (field.getType() == Integer.class || field.getType() == Double.class)) {
                                        switch (field.getType().getSimpleName()) {
                                            case "Integer" -> cell.setCellValue((Integer)returnFromMethod * -1);
                                            case "Double" -> cell.setCellValue((Double)returnFromMethod * -1);
                                        }
                                    } else {
                                        switch (field.getType().getSimpleName()) {
                                            case "Integer" -> cell.setCellValue((Integer)returnFromMethod);
                                            case "Double" -> cell.setCellValue((Double)returnFromMethod);
                                        }
                                    }

                                    if (field.getType() == Double.class || field.getType() == Integer.class) {
                                        sumTotal.set(columnInd - 1,
                                                (!sumTotal.isEmpty() ? sumTotal.get(columnInd - 1) : 0)
                                                        + Double.parseDouble(returnFromMethod.toString())
                                                        * (cellUnit.inverse() ? -1 : 1)
                                        );
                                    }
                                }
                            }
                        }
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        cell.setCellValue(e.getClass().getSimpleName());
                    }
                }
            }

            Integer index = ExcelCellType.getIndex(ExcelCellType.TOTAL);
            Cell cell = row.createCell(columnInd++);
            cell.setCellStyle(cellStyles.get(index != null ? index : 0));
            cell.setCellFormula(
                    "(D2-E2-F2-G2-H2-I2-J2-K2-L2-M2-N2-P2-Q2-U2)/(B2-C2)-2.47"
            );
        }

        Row row = sheet.createRow(rowIndex + 1);
        int columnInd = 0;
        Integer index = ExcelCellType.getIndex(ExcelCellType.TOTAL);
        for (Double sum : sumTotal) {
            Cell cell = row.createCell(columnInd++);
            cell.setCellStyle(cellStyles.get(index != null ? index : 0));
            cell.setCellValue(sum);
        }

        workbook.write(new FileOutputStream(config.getFileName()));
        workbook.close();
    }
}
