package com.example.consul;

import com.example.consul.api.OZON_Api;
import com.example.consul.document.ExcelBuilder;
import com.example.consul.document.configurations.ExcelConfig;
import com.example.consul.document.configurations.HeaderConfig;
import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.services.OZON_Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class OZONApiTest {
    @Autowired
    private OZON_Service ozonService;

    @Test
    public void generateExcel() throws IOException {
        List<OZON_TableRow> data = ozonService.getData(
                "9e98a805-4717-4ea4-a852-41ed1e5948ac",
                "350423",
                "29156444-1716905674931@advertising.performance.ozon.ru",
                "PSCVDiFfWGgV0rWcF6MrA_0gZAW8iyYXH6AFTDk5ZviS4JRETHIBXHLX0033IVnRzr106ULnGks5le2SPg",
                2024, 4);

        ExcelBuilder.createDocument(
                ExcelConfig.<OZON_TableRow>builder()
                        .fileName("2024-04.xls")
                        .header(
                                HeaderConfig.builder()
                                        .title("TEST")
                                        .description("NEW METHOD")
                                        .build()
                        )
                        .data(List.of(data))
                        .sheetsName(List.of("1"))
                        .build()
        );
    }

    @Test
    public void testDetailReport(){
        OZON_Api api = new OZON_Api();
        api.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423");

        System.out.println(api.getDetailReport(4, 2024));
    }

//    @Test
//    public void testDetailReport(){
//        OZON_Api api = new OZON_Api();
//        api.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423");
//
//        System.out.println(api.getDetailReport(4, 2024));
//    }
}
