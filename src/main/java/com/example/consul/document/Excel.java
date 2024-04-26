package com.example.consul.document;

import com.example.consul.api.OZON_Api;
import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.mapping.OZON_dataProcessing;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.apache.poi.ss.usermodel.Font.COLOR_RED;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;


public class Excel {

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
    public void createExcel(String file, String date) throws FileNotFoundException, IOException {

        final OZON_Api api = new OZON_Api();

        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        OZON_DetailReport report = api.getDetailReport(date);
        System.out.println(report.getResult().getRows().size());
        List<OZON_DetailReport.Row> rows = report.getResult().getRows();

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Январь");

        CellStyle style = createBaseStyle(workbook);
        CellStyle styleExpense = createExpenseStyle(workbook);

        Row header = sheet.createRow(0);

        setTableTitle(style, header, sheet, 0, "Код товара поставщика");
        setTableTitle(style, header, sheet, 1, "Доставлено");
        setTableTitle(styleExpense, header, sheet, 2, "Возвращено");
        setTableTitle(style, header, sheet, 3, "Начислено за доставленный товар");
        setTableTitle(styleExpense, header, sheet, 4, "Возврат (-)");
        setTableTitle(style, header, sheet, 5, "Комиссия за продажу (-)");

        Map<String, Integer> mapSaleCount = OZON_dataProcessing.saleCount(OZON_dataProcessing.groupByOfferId(rows));
        System.out.println(mapSaleCount);
        Map<String, Integer> mapReturnCount = OZON_dataProcessing.returnCount(OZON_dataProcessing.groupByOfferId(rows));
        Map<String, Double> mapSaleForDelivered = OZON_dataProcessing.sumSaleForDelivered(OZON_dataProcessing.groupByOfferId(rows));
        Map<String, Double> mapSumReturn = OZON_dataProcessing.sumReturn(OZON_dataProcessing.groupByOfferId(rows));
        Map<String, Double> mapSalesCommission = OZON_dataProcessing.sumSalesCommission(OZON_dataProcessing.groupByOfferId(rows));


        int rowIdx = 1;
        for (Map.Entry<String, Integer> entry : mapSaleCount.entrySet()) {
            Row row = sheet.createRow(rowIdx);

            Cell cell = row.createCell(0);
            cell.setCellValue(entry.getKey());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(entry.getValue());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue(mapReturnCount.getOrDefault(entry.getKey(), 0));
            cell.setCellStyle(styleExpense);

            cell = row.createCell(3);
            cell.setCellValue(mapSaleForDelivered.getOrDefault(entry.getKey(), 0.0));
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue(mapSumReturn.getOrDefault(entry.getKey(), 0.0));
            cell.setCellStyle(styleExpense);

            cell = row.createCell(5);
            cell.setCellValue(mapSalesCommission.getOrDefault(entry.getKey(), 0.0));
            cell.setCellStyle(style);

            rowIdx++;
        }

        workbook.write(new FileOutputStream(file));
        workbook.close();
    }
}