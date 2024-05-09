package com.example.consul;

import com.example.consul.api.YANDEX_Api;
import joinery.DataFrame;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import java.io.*;
import java.util.List;

@SpringBootTest
class YandexMarketTests {


    @Test
    public void DownloadFileTest(){

    }

    @Test
    public void OrdersReportTest() throws IOException {
        final YANDEX_Api api = new YANDEX_Api();
        api.setHeaders("затычка");

        String url = api.getOrdersReport(5731759L,
                "2024-01-01",
                "2024-01-31",
                new ArrayList<>());

        URL orders = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(orders.openStream());
        FileOutputStream fos = new FileOutputStream("Отчет по заказам.xlsx");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        rbc.close();
        fos.close();
    }

    @Test
    public void RealizationReportTest() throws IOException{
        final YANDEX_Api api = new YANDEX_Api();
        api.setHeaders("затычка");

        String url = api.getRealizationReport(23761421L, 2024, 1);

        URL orders = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(orders.openStream());
        FileOutputStream fos = new FileOutputStream("Отчет по реализации.xlsx");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        rbc.close();
        fos.close();

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
