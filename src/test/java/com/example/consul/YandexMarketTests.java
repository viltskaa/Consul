package com.example.consul;

import com.example.consul.api.YANDEX_Api;
import com.example.consul.mapping.YANDEX_dataProcessing;
import com.example.consul.services.OZON_Service;
import com.example.consul.services.YANDEX_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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
        final YANDEX_Api api = new YANDEX_Api();
        api.setHeaders("затычка");

        String url = yandexService.scheduledGetServicesReport(5731759L,
                "2024-02-01",
                "2024-02-29",
                new ArrayList<>());


        URL orders = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(orders.openStream());
        FileOutputStream fos = new FileOutputStream("Отчет по стоимости услуг_февраль.xlsx");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        rbc.close();
        fos.close();
    }

    @Test
    public void RealizationReportTest() throws IOException{
        final YANDEX_Api api = new YANDEX_Api();
        api.setHeaders("затычка");

        String url = yandexService.scheduledGetRealizationReport(23761421L, 2024, 1);

        URL orders = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(orders.openStream());
        FileOutputStream fos = new FileOutputStream("Отчет по реализации.xlsx");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        rbc.close();
        fos.close();

    }

    @Test
    public void readingYandexXls() throws IOException {
        Map<String,List<Integer>> fileMap = new HashMap<>();
        fileMap.put("Отчет по реализации_февраль.xlsx", new ArrayList<>(Arrays.asList(2, 4)));
        fileMap.put("Отчет по стоимости услуг_февраль.xlsx", new ArrayList<>(Arrays.asList(1,3,4,8,11,18)));

        YANDEX_dataProcessing.getAllData(fileMap).writeXls("hello.xls");

        System.out.println(YANDEX_dataProcessing.getAllData(fileMap));
    }
}
