package com.example.consul;

import com.example.consul.api.YANDEX_Api;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
}
