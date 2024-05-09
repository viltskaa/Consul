package com.example.consul;

import com.example.consul.api.YANDEX_Api;
import com.example.consul.mapping.YANDEX_dataProcessing;
import joinery.DataFrame;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import java.io.*;

@SpringBootTest
class YandexMarketTests {


    @Test
    public void DownloadFileTest(){

    }

    @Test
    public void RequestTest() throws IOException {
        final YANDEX_Api api = new YANDEX_Api();
        api.setHeaders("затычка");

        String res = api.getOrdersReport(5731759L,
                "2024-01-01",
                "2024-01-31",
                new ArrayList<>());

        URL website = null;
        try {
            website = new URL("http://www.website.com/information.asp");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("information.html");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        System.out.println(res);
    }

    @Test
    public void readingYandexXls() throws IOException {
        DataFrame<Object> df1 = YANDEX_dataProcessing.getDataFromSheet("statistics-report-2024-01.xls",4);
        DataFrame<Object> df2 = YANDEX_dataProcessing.getDataFromSheet("statistics-report-2024-01.xls",2);
        DataFrame<Object> df3 = YANDEX_dataProcessing.getDataFromSheet("1eff27ea-100f-4870-b5b6-ffea4b4f49b4.xls",2);

        System.out.println(YANDEX_dataProcessing.getReturnData(df1));
        System.out.println(YANDEX_dataProcessing.getDeliveredData(df2));
        System.out.println(YANDEX_dataProcessing.getFavorData(df3,df2));
    }
}
