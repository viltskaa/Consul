package com.example.consul.document;

import com.example.consul.api.OZON_Api;
import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.mapping.OZON_dataProcessing;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class Excel {

    public void setTableTitleName(Row header, Sheet sheet, int columnInd, String titleName){
        Cell cell = header.createCell(columnInd);
        cell.setCellValue(titleName);
        sheet.autoSizeColumn(columnInd);
    }

    public void setTableHeader(Sheet sheet){
        Row header = sheet.createRow(0);

        setTableTitleName(header, sheet, 0, "Код товара поставщика");
        setTableTitleName(header, sheet, 1, "Доставлено");
        setTableTitleName(header, sheet, 2, "Возвращено");
        setTableTitleName(header, sheet, 3, "Начислено за доставленный товар");
        setTableTitleName(header, sheet, 4, "Возврат (-)");
        setTableTitleName(header, sheet, 5, "Комиссия за продажу (-)");
    }

    @SuppressWarnings("deprecation")
    public void createExcel(String file) throws FileNotFoundException, IOException {

        final OZON_Api api = new OZON_Api();
        final OZON_dataProcessing ozonDP = new OZON_dataProcessing();

        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        OZON_DetailReport report =  api.getDetailReport("2024-01");

        List<OZON_DetailReport.Row> rows = report.getResult().getRows();

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet= workbook.createSheet("Январь");

        setTableHeader(sheet);

        Map<String, Integer> mapSaleCount = ozonDP.saleCount(ozonDP.groupByOfferId(rows));
        Map<String, Integer> mapReturnCount = ozonDP.returnCount(ozonDP.groupByOfferId(rows));
        Map<String, Double> mapSaleForDelivered = ozonDP.sumSaleForDelivered(ozonDP.groupByOfferId(rows));
        Map<String, Double> mapSumReturn = ozonDP.sumReturn(ozonDP.groupByOfferId(rows));
        Map<String, Double> mapSalesCommission = ozonDP.sumSalesCommission(ozonDP.groupByOfferId(rows));

        int rowIdx = 1;
        for (Map.Entry<String, Integer> entry : mapSaleCount.entrySet()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
            row.createCell(2).setCellValue(mapReturnCount.getOrDefault(entry.getKey(), 0));
            row.createCell(3).setCellValue(mapSaleForDelivered.getOrDefault(entry.getKey(), 0.0));
            row.createCell(4).setCellValue(mapSumReturn.getOrDefault(entry.getKey(), 0.0));
            row.createCell(5).setCellValue(mapSalesCommission.getOrDefault(entry.getKey(), 0.0));
            rowIdx++;
        }

        workbook.write(new FileOutputStream(file));
        workbook.close();
    }
}