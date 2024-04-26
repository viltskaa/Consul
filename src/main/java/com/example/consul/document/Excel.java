package com.example.consul.document;

import com.example.consul.services.ExcelService;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.apache.poi.ss.usermodel.Font.COLOR_RED;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;

public class Excel {

    private final ExcelService excelService;

    public Excel(ExcelService excelService) {
        this.excelService=excelService;
    }

    public CellStyle createBaseStyle(Workbook workbook) {
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

    public CellStyle createExpenseStyle(Workbook workbook) {
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

    public void setTableTitle(CellStyle style, Row header, Sheet sheet, int columnInd, String titleName) {
        Cell cell = header.createCell(columnInd);
        cell.setCellValue(titleName);
        cell.setCellStyle(style);
        sheet.setColumnWidth(columnInd, 15 * 256);
    }

    @SuppressWarnings("deprecation")
    public void createExcel(String file, String apiKey, String clientId,  String date, String from, String to) throws FileNotFoundException, IOException {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(excelService.getMonthNameAndYear(date));

        CellStyle style = createBaseStyle(workbook);
        CellStyle styleExpense = createExpenseStyle(workbook);

        Row header = sheet.createRow(0);

        setTableTitle(style, header, sheet, 0, "Код товара поставщика");
        setTableTitle(style, header, sheet, 1, "Доставлено");
        setTableTitle(styleExpense, header, sheet, 2, "Возвращено");
        setTableTitle(style, header, sheet, 3, "Начислено за доставленный товар");
        setTableTitle(styleExpense, header, sheet, 4, "Возврат (-)");
        setTableTitle(style, header, sheet, 5, "Комиссия за продажу (-)");
        setTableTitle(styleExpense, header, sheet, 6, "обработка отправления");
        setTableTitle(styleExpense, header, sheet, 7, "Логистика (до покупателя)");
        setTableTitle(style, header, sheet, 8, "Последняя миля");
        setTableTitle(style, header, sheet, 9, "Эквайринг");
        setTableTitle(style, header, sheet, 10, "Обработка возврата");
        setTableTitle(style, header, sheet, 11, "Доставка возврата");

        int rowIdx = 1;
        for (Map.Entry<String, Integer> entry : excelService.getMapSaleCount(apiKey, clientId,date).entrySet()) {
            Row row = sheet.createRow(rowIdx);

            Cell cell = row.createCell(0);
            cell.setCellValue(entry.getKey());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(entry.getValue());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue(excelService
                    .getMapReturnCount(apiKey, clientId,date)
                    .getOrDefault(entry.getKey(), 0));
            cell.setCellStyle(styleExpense);

            cell = row.createCell(3);
            cell.setCellValue(excelService
                    .getMapSaleForDelivered(apiKey, clientId,date)
                    .getOrDefault(entry.getKey(), 0.0));
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue(excelService
                    .getMapSumReturn(apiKey, clientId,date)
                    .getOrDefault(entry.getKey(), 0.0));
            cell.setCellStyle(styleExpense);

            cell = row.createCell(5);
            cell.setCellValue(excelService
                    .getMapSalesCommission(apiKey, clientId,date)
                    .getOrDefault(entry.getKey(), 0.0));
            cell.setCellStyle(style);

            cell = row.createCell(6);
            cell.setCellValue(excelService
                    .getMapShipmentProcessing(apiKey, clientId,date, from, to)
                    .getOrDefault(entry.getKey(), 0.0)*(-1));
            cell.setCellStyle(styleExpense);

            cell = row.createCell(7);
            cell.setCellValue(excelService
                    .getMapLogistic(apiKey, clientId,date, from, to)
                    .getOrDefault(entry.getKey(), 0.0)*(-1));
            cell.setCellStyle(styleExpense);

            cell = row.createCell(8);
            cell.setCellValue(excelService
                    .getMapLastMile(apiKey, clientId,date, from, to)
                    .getOrDefault(entry.getKey(), 0.0)*(-1));
            cell.setCellStyle(style);

            cell = row.createCell(9);
            cell.setCellValue(excelService
                    .getMapAcquiring(apiKey, clientId,date, from, to)
                    .getOrDefault(entry.getKey(), 0.0)*(-1));
            cell.setCellStyle(style);

            cell = row.createCell(10);
            cell.setCellValue(excelService
                    .getMapReturnProcessing(apiKey, clientId,date, from, to)
                    .getOrDefault(entry.getKey(), 0.0)*(-1));
            cell.setCellStyle(style);

            cell = row.createCell(11);
            cell.setCellValue(excelService
                    .getMapReturnDelivery(apiKey, clientId,date, from, to)
                    .getOrDefault(entry.getKey(), 0.0)*(-1));
            cell.setCellStyle(style);

            rowIdx++;
        }

        workbook.write(new FileOutputStream(file));
        workbook.close();
    }
}