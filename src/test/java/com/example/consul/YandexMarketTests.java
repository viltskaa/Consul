package com.example.consul;

import com.example.consul.api.YANDEX_Api;
import joinery.DataFrame;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class YandexMarketTests {


    @Test
    public void DownloadFileTest(){

    }

    @Test
    public void RequestTest(){
        final YANDEX_Api api = new YANDEX_Api();
        api.setHeaders("затычка");

        String res = api.getOrdersReport(5731759L,
                "2024-01-01",
                "2024-01-31",
                new ArrayList<>());

        System.out.println(res);
    }

    @Test
    public void readingYandexXls() throws IOException {
        File file = new File("statistics-report-2024-01.xls");
        Workbook wb = WorkbookFactory.create(file);
        Sheet sheet = wb.getSheetAt(1);
        List<String> listHeader = new ArrayList<>();
        List<Object> listData = new ArrayList<>();

        for(int i = 0;i < sheet.getRow(13).getPhysicalNumberOfCells(); i++){
            listHeader.add(sheet.getRow(13).getCell(i).getStringCellValue());
        }

        DataFrame<Object> df = new DataFrame<>(listHeader);

        for(int j = 14; j < sheet.getPhysicalNumberOfRows() - 1; j++){
            for(int i = 0; i < sheet.getRow(j).getPhysicalNumberOfCells(); i++){
                switch (sheet.getRow(j).getCell(i).getCellType()) {
                    case BOOLEAN:
                        listData.add(sheet.getRow(j).getCell(i).getBooleanCellValue());
                        break;
                    case NUMERIC:
                        listData.add(sheet.getRow(j).getCell(i).getNumericCellValue());
                        break;
                    case STRING:
                        listData.add(sheet.getRow(j).getCell(i).getRichStringCellValue());
                        break;
                }
            }
            df.append(listData);
            listData = new ArrayList<>();
        }

        System.out.println(df);

    }
}
