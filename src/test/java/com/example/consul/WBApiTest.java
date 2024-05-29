package com.example.consul;

import com.example.consul.document.ExcelBuilder;
import com.example.consul.document.configurations.ExcelConfig;
import com.example.consul.document.configurations.HeaderConfig;
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

    @Test
    void createDataTest() {
        List<WB_TableRow> rows = service.getData(
                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjMyOSwiaWQiOiJkYzEwOTdkMS1jYTQ1LTRjZWMtYTQyOC0zNThiM2FhMDFiZjUiLCJpaWQiOjExOTExMzYzNiwib2lkIjo1OTU3MzQsInMiOjEwMjIsInNpZCI6IjdhZmRlMmI4LWM1ZGQtNGNmOC1iOTBmLTY3MGUxYzcxMmI5YSIsInQiOmZhbHNlLCJ1aWQiOjExOTExMzYzNn0.BIjm0DrZkyXtKu5_NdZ5fGoUQmhD6uzHnexGE1KtdMzznW6agpmUsiPRkh4I9xtxVBBRy6TSu_syn8Fj-jP-7g",
                2024,
                2
        );
        rows.forEach(System.out::println);
    }

    @Test
    void createExcelTest() throws IOException {
        List<WB_TableRow> rows = service.getData(
                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjMyOSwiaWQiOiJkYzEwOTdkMS1jYTQ1LTRjZWMtYTQyOC0zNThiM2FhMDFiZjUiLCJpaWQiOjExOTExMzYzNiwib2lkIjo1OTU3MzQsInMiOjEwMjIsInNpZCI6IjdhZmRlMmI4LWM1ZGQtNGNmOC1iOTBmLTY3MGUxYzcxMmI5YSIsInQiOmZhbHNlLCJ1aWQiOjExOTExMzYzNn0.BIjm0DrZkyXtKu5_NdZ5fGoUQmhD6uzHnexGE1KtdMzznW6agpmUsiPRkh4I9xtxVBBRy6TSu_syn8Fj-jP-7g",
                2024,
                2
        );
        ExcelBuilder.createDocument(
                ExcelConfig.<WB_TableRow>builder()
                        .fileName("WB202401.xls")
                        .data(List.of(rows))
                        .header(HeaderConfig.builder()
                                .title("NEW WB")
                                .description("2024-01").build())
                        .sheetsName(List.of("1"))
                        .build()
        );
    }

    @Test
    void createDataByWeekTest() {
        List<WB_TableRow> rows = service.getData(
                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjMyOSwiaWQiOiJkYzEwOTdkMS1jYTQ1LTRjZWMtYTQyOC0zNThiM2FhMDFiZjUiLCJpaWQiOjExOTExMzYzNiwib2lkIjo1OTU3MzQsInMiOjEwMjIsInNpZCI6IjdhZmRlMmI4LWM1ZGQtNGNmOC1iOTBmLTY3MGUxYzcxMmI5YSIsInQiOmZhbHNlLCJ1aWQiOjExOTExMzYzNn0.BIjm0DrZkyXtKu5_NdZ5fGoUQmhD6uzHnexGE1KtdMzznW6agpmUsiPRkh4I9xtxVBBRy6TSu_syn8Fj-jP-7g",
                2024,
                2,
                1
        );
        rows.forEach(System.out::println);
    }

    @Test
    void createExcelByWeekTest() throws IOException {
        List<WB_TableRow> rows = service.getData(
                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjMyOSwiaWQiOiJkYzEwOTdkMS1jYTQ1LTRjZWMtYTQyOC0zNThiM2FhMDFiZjUiLCJpaWQiOjExOTExMzYzNiwib2lkIjo1OTU3MzQsInMiOjEwMjIsInNpZCI6IjdhZmRlMmI4LWM1ZGQtNGNmOC1iOTBmLTY3MGUxYzcxMmI5YSIsInQiOmZhbHNlLCJ1aWQiOjExOTExMzYzNn0.BIjm0DrZkyXtKu5_NdZ5fGoUQmhD6uzHnexGE1KtdMzznW6agpmUsiPRkh4I9xtxVBBRy6TSu_syn8Fj-jP-7g",
                2024,
                2,
                1
        );
        ExcelBuilder.createDocument(
                ExcelConfig.<WB_TableRow>builder()
                        .fileName("WBY2024M01W1.xls")
                        .data(List.of(rows))
                        .header(HeaderConfig.builder()
                                .title("NEW WB")
                                .description("2024-01").build())
                        .sheetsName(List.of("1"))
                        .build()
        );
    }
}
