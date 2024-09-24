package com.example.consul;

import com.example.consul.document.v1.ExcelBuilderV1;
import com.example.consul.document.v1.configurations.ExcelConfig;
import com.example.consul.document.v1.configurations.HeaderConfig;
import com.example.consul.document.models.WB_TableRow;
import com.example.consul.services.WB_Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class WBApiTest {
    @Autowired
    private WB_Service service;

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
//    @Test
//    void createExcelTest() throws IOException {
//        List<WB_TableRow> rows = service.getData(
//                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjMyOSwiaWQiOiJkYzEwOTdkMS1jYTQ1LTRjZWMtYTQyOC0zNThiM2FhMDFiZjUiLCJpaWQiOjExOTExMzYzNiwib2lkIjo1OTU3MzQsInMiOjEwMjIsInNpZCI6IjdhZmRlMmI4LWM1ZGQtNGNmOC1iOTBmLTY3MGUxYzcxMmI5YSIsInQiOmZhbHNlLCJ1aWQiOjExOTExMzYzNn0.BIjm0DrZkyXtKu5_NdZ5fGoUQmhD6uzHnexGE1KtdMzznW6agpmUsiPRkh4I9xtxVBBRy6TSu_syn8Fj-jP-7g",
//                2024,
//                4
//        );
//        ExcelBuilderV1.createDocument(
//                ExcelConfig.<WB_TableRow>builder()
//                        .fileName("WB202404.xls")
//                        .data(List.of(rows))
//                        .header(HeaderConfig.builder()
//                                .title("NEW WB")
//                                .description("2024-04").build())
//                        .sheetsName(List.of("1"))
//                        .build()
//        );
//    }
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
