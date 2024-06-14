package com.example.consul;

import com.example.consul.document.ExcelBuilder;
import com.example.consul.document.configurations.ExcelConfig;
import com.example.consul.document.configurations.HeaderConfig;
import com.example.consul.document.models.YANDEX_TableRow;
import com.example.consul.services.YANDEX_Service;
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
