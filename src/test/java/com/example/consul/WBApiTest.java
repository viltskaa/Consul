package com.example.consul;

import com.example.consul.document.models.WB_TableRow;
import com.example.consul.services.WB_Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class WBApiTest {
    @Autowired
    private WB_Service _service;

    public static void createExcel(List<WB_TableRow> data, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отчет");

        // Создание заголовка таблицы
        String[] headers = {"Артикул", "Доставлено", "Начислено за товар", "Возвращено", "Стоимость возврата",
                "ИТОГО", "Комиссия маркетплейса", "Штрафы", "Прочие удержания", "Логистика", "Хранение",
                "Логистика сторно", "Итого комиссия маркетплейса"};
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (WB_TableRow rowDetail : data) {
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowDetail.getArticle());
            row.createCell(1).setCellValue(rowDetail.getSaleCount());
            row.createCell(2).setCellValue(rowDetail.getSaleSum());
            row.createCell(3).setCellValue(rowDetail.getReturnCount());
            row.createCell(4).setCellValue(rowDetail.getReturnSum());

            // ИТОГО (saleSum - returnSum)
            row.createCell(5).setCellFormula("C" + (rowNum + 1) + "-E" + (rowNum + 1));

            row.createCell(6).setCellValue(rowDetail.getCommission());
            row.createCell(7).setCellValue(rowDetail.getPenalty());
            row.createCell(8).setCellValue(rowDetail.getDeduction());
            row.createCell(9).setCellValue(rowDetail.getLogistic());
            row.createCell(10).setCellValue(rowDetail.getStorageFee());
            row.createCell(11).setCellValue(rowDetail.getStorno());

            // Итого комиссия маркетплейса: СУММ(D27:J27)+A27
            row.createCell(12).setCellFormula("SUM(G" + (rowNum + 1) + ":L" + (rowNum + 1) + ")+C" + (rowNum + 1));

            rowNum++;
        }

        // Автоматическая настройка ширины колонок
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

//    @Test
//    void createDataTest() {
//        List<WB_TableRow> rows = service.getData(
//                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjMyOSwiaWQiOiJkYzEwOTdkMS1jYTQ1LTRjZWMtYTQyOC0zNThiM2FhMDFiZjUiLCJpaWQiOjExOTExMzYzNiwib2lkIjo1OTU3MzQsInMiOjEwMjIsInNpZCI6IjdhZmRlMmI4LWM1ZGQtNGNmOC1iOTBmLTY3MGUxYzcxMmI5YSIsInQiOmZhbHNlLCJ1aWQiOjExOTExMzYzNn0.BIjm0DrZkyXtKu5_NdZ5fGoUQmhD6uzHnexGE1KtdMzznW6agpmUsiPRkh4I9xtxVBBRy6TSu_syn8Fj-jP-7g",
//                2024,
//                2
//        );
//        rows.forEach(System.out::println);
//    }
//
    @Test
    void createExcelTest() throws IOException {
        Map<String, List<WB_TableRow>> rows = _service.getData(
                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjUwMTIwdjEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTc1MzkxNDIwMCwiaWQiOiIwMTk0YjE5Ni0zZDQ2LTdkYjktOTJkNC02YTU2MTEwYWEzYWMiLCJpaWQiOjExOTExMzYzNiwib2lkIjo1OTU3MzQsInMiOjQxMjgsInNpZCI6IjdhZmRlMmI4LWM1ZGQtNGNmOC1iOTBmLTY3MGUxYzcxMmI5YSIsInQiOmZhbHNlLCJ1aWQiOjExOTExMzYzNn0.dEJ-DMmSd4bCgD2dutG-fmwvUSt9XGT6k_nDnJ4-EbX4j0G7qDHyjIyrQi-rAPQHoLfFMfqC04aSYu_WDMomRg",
                2024,
                4
        );
        createExcel(rows.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList()), "file1.xlsx");
    }
//
//    @Test
//    void createDataByWeekTest() {
//        List<WB_TableRow> rows = service.getData(
//                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjMyOSwiaWQiOiJkYzEwOTdkMS1jYTQ1LTRjZWMtYTQyOC0zNThiM2FhMDFiZjUiLCJpaWQiOjExOTExMzYzNiwib2lkIjo1OTU3MzQsInMiOjEwMjIsInNpZCI6IjdhZmRlMmI4LWM1ZGQtNGNmOC1iOTBmLTY3MGUxYzcxMmI5YSIsInQiOmZhbHNlLCJ1aWQiOjExOTExMzYzNn0.BIjm0DrZkyXtKu5_NdZ5fGoUQmhD6uzHnexGE1KtdMzznW6agpmUsiPRkh4I9xtxVBBRy6TSu_syn8Fj-jP-7g",
//                2024,
//                2,
//                1
//        );
//        rows.forEach(System.out::println);
//    }
//
//    @Test
//    void createExcelByWeekTest() throws IOException {
//        List<WB_TableRow> rows = service.getData(
//                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjIyNCwiaWQiOiIzNDRlMzA1Ni1jMDU4LTQxMmEtODk3Zi1kZjJkYTdiNDdiYjQiLCJpaWQiOjQ1ODkwNDkwLCJvaWQiOjg5NzE2NiwicyI6MTAyMiwic2lkIjoiMTZhMGZiZWEtYWVmZi00YjgxLThmNzEtZjYyZDlhYjJmMGM1IiwidCI6ZmFsc2UsInVpZCI6NDU4OTA0OTB9.QL4J2FabaLOHCdPovbyaUWKw28VdRbruv-PY1m5tLhWea_0DEcExywqEvwcRAiHfQyNOydOJe2biakFg68iH9Q",
////                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjMyOSwiaWQiOiJkYzEwOTdkMS1jYTQ1LTRjZWMtYTQyOC0zNThiM2FhMDFiZjUiLCJpaWQiOjExOTExMzYzNiwib2lkIjo1OTU3MzQsInMiOjEwMjIsInNpZCI6IjdhZmRlMmI4LWM1ZGQtNGNmOC1iOTBmLTY3MGUxYzcxMmI5YSIsInQiOmZhbHNlLCJ1aWQiOjExOTExMzYzNn0.BIjm0DrZkyXtKu5_NdZ5fGoUQmhD6uzHnexGE1KtdMzznW6agpmUsiPRkh4I9xtxVBBRy6TSu_syn8Fj-jP-7g",
//                2024,
//                4,
//                2
//        );
//        ExcelBuilderV1.createDocument(
//                ExcelConfig.<WB_TableRow>builder()
//                        .fileName("WBY2024M04W2_z.xls")
//                        .data(List.of(rows))
//                        .header(HeaderConfig.builder()
//                                .title("NEW WB")
//                                .description("2024-04 2w").build())
//                        .sheetsName(List.of("1"))
//                        .build()
//        );
//    }
}
