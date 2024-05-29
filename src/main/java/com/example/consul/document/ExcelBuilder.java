package com.example.consul.document;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.annotations.TotalCell;
import com.example.consul.document.configurations.ExcelCellType;
import com.example.consul.document.configurations.ExcelConfig;
import com.example.consul.document.configurations.HeaderConfig;
import com.example.consul.document.models.TableRow;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
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

    private ExcelBuilder() {
    }

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
        fontExpense.setColor(COLOR_RED);
        style.setFont(fontExpense);
        return style;
    }

    @NotNull
    private static CellStyle createTotalStyle(@NotNull Workbook workbook) {
        CellStyle style = createBaseStyle(workbook);
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static void addHeader(@NotNull Sheet sheet, @NotNull HeaderConfig header,
                                  int firstRow, int lastRow,
                                  int firstColumn, int lastColumn) {
        for (int i = firstRow; i < lastRow; i++) {
            Row row = sheet.createRow(i);
            for (int j = firstColumn; j < lastColumn; j++) {
                row.createCell(j);
            }
        }

        Cell cell = sheet.getRow(firstRow).getCell(firstColumn);

        CellStyle style = createBaseStyle(sheet.getWorkbook());
        Font font = sheet.getWorkbook().createFont();
        font.setFontHeightInPoints((short) 24);
        style.setFont(font);
        cell.setCellStyle(style);
        cell.setCellValue(header.getTitle() + "\n" + header.getDescription());
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);

        sheet.addMergedRegion(
                new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn)
        );
    }

    private static void setTableTitle(@NotNull CellStyle style, @NotNull Row row, @NotNull Sheet sheet,
                                      int columnInd, String titleName, int width) {
        Cell cell = row.createCell(columnInd);
        cell.setCellValue(titleName);
        cell.setCellStyle(style);
        sheet.setColumnWidth(columnInd, width * 256);
    }

    private static void addPageHeader(@NotNull Sheet sheet, @NotNull List<Field> fields,
                                      int rowIndex, int columnIndex, boolean total) {
        Row header = sheet.createRow(rowIndex);

        for (Field field : fields) {
            if (field.isAnnotationPresent(CellUnit.class)) {
                CellUnit cellUnit = field.getAnnotation(CellUnit.class);
                Integer index = ExcelCellType.getIndex(cellUnit.type());
                setTableTitle(cellStyles.get(index != null ? index : 0),
                        header,
                        sheet,
                        columnIndex++,
                        cellUnit.name(),
                        cellUnit.width());
            }
        }
    }

    private static List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            fields.addAll(0, Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    private static List<Method> getMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            methods.addAll(0, Arrays.asList(c.getDeclaredMethods()));
        }
        return methods;
    }

    private static <R> void createSheet(@NotNull Workbook workbook,
                                        @NotNull List<R> data,
                                        @NotNull HeaderConfig headerConfig,
                                        @NotNull String name) {
        Sheet sheet = workbook.createSheet(name);

        Class<?> clazz = data.get(0).getClass();
        List<Field> fields = getFields(clazz);
        List<Method> methods = getMethods(clazz).stream()
                .filter(x -> !x.getAnnotatedReturnType().getType().equals(void.class)).toList();

        List<Pair<Field, Method>> fieldMethodList = fields.stream()
                .map(x -> new Pair<>(x, methods.stream()
                        .filter(y -> y.getName().equals("get" + x.getName().substring(0, 1).toUpperCase() + x.getName().substring(1)))
                        .findFirst().orElse(null))).toList();

        addHeader(sheet, headerConfig, 0, 4, 0, fields.size());
        addPageHeader(sheet, fields, 5, 0, clazz.isAnnotationPresent(TotalCell.class));
        int rowIndex = 6;

        for (R obj : data) {
            Row row = sheet.createRow(rowIndex++);
            int columnIndex = 0;
            String formula = obj.getClass().getAnnotation(TotalCell.class).formula();

            for (Pair<Field, Method> fieldMethodPair : fieldMethodList) {
                Field field = fieldMethodPair.getKey();
                if (field.isAnnotationPresent(CellUnit.class)) {
                    CellUnit cellUnit = field.getAnnotation(CellUnit.class);
                    Cell cell = row.createCell(columnIndex);

                    Integer index = ExcelCellType.getIndex(cellUnit.type());
                    cell.setCellStyle(cellStyles.get(index != null ? index : 0));

                    if (field.getName().equals("total") && formula != null) {
                        cell.setCellFormula(formula);
                        continue;
                    }

                    if (formula != null) {
                        formula = formula.replace(field.getName(),
                                String.valueOf((char) (65 + columnIndex)) + (rowIndex));
                    }

                    Method method = fieldMethodPair.getValue();
                    if (method == null) {
                        cell.setCellValue(cellUnit.defaultValue());
                    }
                    else {
                        try {
                            Object returnFromMethod = method.invoke(obj);
                            if (returnFromMethod == null) {
                                cell.setCellValue(cellUnit.defaultValue());
                            }
                            else {
                                switch (returnFromMethod.getClass().getSimpleName()) {
                                    case "String" -> cell.setCellValue(returnFromMethod.toString());
                                    case "Integer" -> cell.setCellValue((Integer)returnFromMethod);
                                    case "Double" -> cell.setCellValue((Double)returnFromMethod);
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            cell.setCellValue(cellUnit.defaultValue());
                        }
                    }
                }
                columnIndex++;
            }
        }

        Row row = sheet.createRow(rowIndex);
        int columnInd = 0;
        Integer index = ExcelCellType.getIndex(ExcelCellType.TOTAL);
        for (Field field : fields) {
            if (field.getAnnotation(CellUnit.class).total()) {
                StringBuilder formula = new StringBuilder();
                for (int i = 0; i < data.size(); i++) {
                    formula.append((char) (65 + columnInd))
                            .append(i + 7);
                    if (i != data.size() - 1) {
                        formula.append("+");
                    }
                }

                Cell cell = row.createCell(columnInd++);
                cell.setCellStyle(cellStyles.get(index != null ? index : 0));
                cell.setCellFormula(formula.toString());
            }
            else {
                columnInd++;
            }
        }
    }

    private static <T> Workbook createWorkbook(@NotNull ExcelConfig<T> config) {
        Workbook workbook = new HSSFWorkbook();
        cellStyles = List.of(
                createBaseStyle(workbook),
                createExpenseStyle(workbook),
                createTotalStyle(workbook)
        );

        for (int i = 0 ; i < config.getPageNumber() ; i++) {
            List<T> obj = config.getData().get(i);
            if (obj.isEmpty()) continue;

            createSheet(workbook,
                    obj,
                    config.getHeader(),
                    config.getSheetsName().get(i));
        }

        return workbook;
    }

    public static <T> void createDocument(@NotNull ExcelConfig<T> config) throws IOException {
        Workbook workbook = createWorkbook(config);

        workbook.write(new FileOutputStream(config.getFileName()));
        workbook.close();
    }

    public static <T> byte[] createDocumentToByteArray(@NotNull ExcelConfig<T> config) {
        Workbook workbook = createWorkbook(config);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            bos.close();
        }
        catch (IOException ioException) {
            return null;
        }
        return bos.toByteArray();
    }
}
