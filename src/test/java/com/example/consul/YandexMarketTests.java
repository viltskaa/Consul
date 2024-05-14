package com.example.consul;

import com.example.consul.mapping.YANDEX_dataProcessing;
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
    public void DownloadFileTest(){

    }

    @Test
    public void ServicesReportTest() throws IOException {
        yandexService.setHeaders("y0_AgAAAABzBvISAAu7EwAAAAED4UtWAAAtEQmj-qVJyrHP6B9zqdC6RMWeeA");

        String url = yandexService.scheduledGetServicesReport(5731759L,
                "2024-02-01",
                "2024-02-29",
                new ArrayList<>());

        URL orders = new URL(url);
        InputStream inputStream = new ByteArrayInputStream(orders.openStream().readAllBytes());

        System.out.println(YANDEX_dataProcessing.getDataFromServiceInputStream(inputStream));

        inputStream.close();
    }

    @Test
    public void RealizationReportTest() throws IOException {
        yandexService.setHeaders("y0_AgAAAABzBvISAAu7EwAAAAED4UtWAAAtEQmj-qVJyrHP6B9zqdC6RMWeeA");

        String url = yandexService.scheduledGetRealizationReport(23761421L, 2024, 1);

        URL orders = new URL(url);
        InputStream inputStream = new ByteArrayInputStream(orders.openStream().readAllBytes());

        System.out.println(YANDEX_dataProcessing.getDataFromRealizationInputStream(inputStream));

        inputStream.close();
    }
}
