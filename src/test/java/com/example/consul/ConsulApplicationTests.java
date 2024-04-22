package com.example.consul;

import com.example.consul.api.OZON_Api;
import com.example.consul.dto.OZON_DetailReport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ConsulApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void DetailReportTest(){
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        OZON_DetailReport report =  api.getDetailReport("2024-01");
        List<OZON_DetailReport.Row> rows = report.getResult().getRows();
        double sum = 0;
        for(OZON_DetailReport.Row row: rows){
            if(row.getOffer_id().equals("RO010"))

                sum += row.getPrice() * row.getSale_qty();
        }
        System.out.println(sum);
    }

}
