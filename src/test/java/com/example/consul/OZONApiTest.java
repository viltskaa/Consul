package com.example.consul;

import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.services.OZON_ExcelCreator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class OZONApiTest {
    @Autowired
    private OZON_ExcelCreator excelService;

    @Test
    public void getRowsTest() {
//        List<OZON_TableRow> data = excelService.mergeMapsToTableRows(
//                "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
//                "350423",
//                "27013136-1713353681106@advertising.performance.ozon.ru",
//                "w8jTBuPxzAr5iW2dvioeroGh_7aDVHOyS8LhwD4lzK2x5kUQeYytrJ7HeD4yEygPU2iAO9AaU-XOdV7Z1Q",
//                1, 2024);
//        System.out.println(data);
    }
}
