package com.example.consul;

import com.example.consul.document.ExcelBuilder;
import com.example.consul.document.configurations.ExcelConfig;
import com.example.consul.document.configurations.HeaderConfig;
import com.example.consul.document.models.YANDEX_TableRow;
import com.example.consul.mapping.YANDEX_dataProcessing;
import com.example.consul.mapping.sheets.YANDEX_BoostSales;
import com.example.consul.services.YANDEX_Service;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.*;

import java.io.*;

@SpringBootTest
class YandexMarketTests {
    @Autowired
    private YANDEX_Service yandexService;

    @Test
    public void DownloadFileTest() {

    }

    @Test
    public void getMapDelivery() throws FileNotFoundException {
        String excelFilePath = "ru-statistics-report-2024-04 (2).xlsx";

        try (InputStream inputStream = new FileInputStream(excelFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(2);

            System.out.println(YANDEX_dataProcessing.getMapDeliveryCount(sheet));
            System.out.println(YANDEX_dataProcessing.getMapDeliveryCost(sheet));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getMapReturn() throws FileNotFoundException {
        String excelFilePath = "ru-statistics-report-2024-04 (2).xlsx";

        try (InputStream inputStream = new FileInputStream(excelFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(4);

            System.out.println(YANDEX_dataProcessing.getMapReturnCost(sheet));
            System.out.println(YANDEX_dataProcessing.getMapReturnCount(sheet));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getMapPlacingOnShowcase() throws FileNotFoundException {
        String excelFilePath = "united-marketplace-services-138fc204-bbaa-4e13-8499-85f6ea966537.xlsx";

        try (InputStream inputStream = new FileInputStream(excelFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(1);

            System.out.println(YANDEX_dataProcessing.getMapPlacingOnShowcase(sheet));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getMapSortingCenter() throws FileNotFoundException {
        String excelFilePath = "united-marketplace-services-e89729bc-f5b6-4f01-bdf8-74392c745c98.xlsx";

        try (InputStream inputStream = new FileInputStream(excelFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet1 = workbook.getSheetAt(8);
            Sheet sheet3 = workbook.getSheetAt(11);
            Sheet sheet2 = workbook.getSheetAt(18);

//            System.out.println(YANDEX_dataProcessing.getMapSortingCenter(sheet1, sheet2, sheet3));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getMapDeliveryToConsumer() throws FileNotFoundException {
        String excelFilePath = "united-marketplace-services-138fc204-bbaa-4e13-8499-85f6ea966537.xlsx";

        try (InputStream inputStream = new FileInputStream(excelFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(8);

            System.out.println(YANDEX_dataProcessing.getMapDeliveryToConsumer(sheet));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAcceptAndTransferPayment() throws FileNotFoundException {
        String excelFilePath = "united-marketplace-services-138fc204-bbaa-4e13-8499-85f6ea966537.xlsx";

        try (InputStream inputStream = new FileInputStream(excelFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet1 = workbook.getSheetAt(11);
            Sheet sheet2 = workbook.getSheetAt(12);

            System.out.println(YANDEX_dataProcessing.getMapAcceptAndTransferPayment(sheet1, sheet2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getMapLoyaltyProgram() throws FileNotFoundException {
        String excelFilePath = "united-marketplace-services-138fc204-bbaa-4e13-8499-85f6ea966537.xlsx";
        int sheetIndex = 3;

        try (InputStream inputStream = new FileInputStream(excelFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);

            System.out.println(YANDEX_dataProcessing.getMapLoyaltyProgram(sheet));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetMapBoostSales() throws FileNotFoundException {
        String excelFilePath = "united-marketplace-services-546509f2-f194-4fa3-8e79-db2a912e7d98.xlsx";
        int sheetIndex = 4;

        try (InputStream inputStream = new FileInputStream(excelFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);

            System.out.println(YANDEX_dataProcessing.getMapBoostSales(sheet));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void ServicesReportTest() throws IOException {

        String url = yandexService.scheduledGetServicesReport("y0_AgAAAABzBvISAAu7EwAAAAED4UtWAAAtEQmj-qVJyrHP6B9zqdC6RMWeeA", 5731759L, "2024-02-01", "2024-02-29");

        URL orders = new URL(url);
        InputStream inputStream = new ByteArrayInputStream(orders.openStream().readAllBytes());

        System.out.println(inputStream);

        inputStream.close();
    }

    @Test
    public void RealizationReportTest() throws IOException {
        String url = yandexService.scheduledGetRealizationReport("y0_AgAAAABzBvISAAu7EwAAAAED4UtWAAAtEQmj-qVJyrHP6B9zqdC6RMWeeA", 23761421L, 2024, 1);

        URL orders = new URL(url);
        InputStream inputStream = new ByteArrayInputStream(orders.openStream().readAllBytes());

        System.out.println(inputStream);

        inputStream.close();
    }

    @Test
    public void generateExcel() throws IOException {
        List<YANDEX_TableRow> data = yandexService.getData("y0_AgAAAABzBvISAAu7EwAAAAED4UtWAAAtEQmj-qVJyrHP6B9zqdC6RMWeeA", 23761421L, 5731759L, 2024, 2);

        ExcelBuilder.createDocument(ExcelConfig.<YANDEX_TableRow>builder().fileName("2024-02.xls").header(HeaderConfig.builder().title("TEST").description("NEW METHOD").build()).data(List.of(data)).sheetsName(List.of("1")).build());
    }
}
