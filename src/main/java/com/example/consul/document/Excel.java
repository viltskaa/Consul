package com.example.consul.document;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
public class Excel {
    @SuppressWarnings("deprecation")
    public void createExcel(String file) throws FileNotFoundException, IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet= workbook.createSheet("Январь");

        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellValue("Код товара поставщика");

        cell = row.createCell(1);
        cell.setCellValue("Доставлено");

        cell = row.createCell(2);
        cell.setCellValue("Возвращено");

        cell = row.createCell(3);
        cell.setCellValue("Начислено за доставленный товар");

        cell = row.createCell(4);
        cell.setCellValue("Возврат (-)");

        cell = row.createCell(5);
        cell.setCellValue("Комиссия за продажу (-)");

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        workbook.write(new FileOutputStream(file));
        workbook.close();
    }
}