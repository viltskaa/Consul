package com.example.consul;

import com.example.consul.conditions.ConditionalWithDelayChecker;
import com.example.consul.document.ExcelBuilder;
import com.example.consul.document.configurations.ExcelConfig;
import com.example.consul.document.configurations.HeaderConfig;
import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.services.OZON_Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class OZONApiTest {
    @Autowired
    private OZON_Service ozonService;

    @Test
    public void getRowsTest() {
        List<OZON_TableRow> data = ozonService.mergeMapsToTableRows(
                "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
                "350423",
                "27013136-1713353681106@advertising.performance.ozon.ru",
                "w8jTBuPxzAr5iW2dvioeroGh_7aDVHOyS8LhwD4lzK2x5kUQeYytrJ7HeD4yEygPU2iAO9AaU-XOdV7Z1Q",
                2024, 1);
        System.out.println(data);
    }

    @Test
    public void getAsyncTest() {
        long start = System.currentTimeMillis();
        ozonService.mergeMapsToTableRows(
                "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
                "350423",
                "27013136-1713353681106@advertising.performance.ozon.ru",
                "w8jTBuPxzAr5iW2dvioeroGh_7aDVHOyS8LhwD4lzK2x5kUQeYytrJ7HeD4yEygPU2iAO9AaU-XOdV7Z1Q",
                2024, 1);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println(timeElapsed);

        start = System.currentTimeMillis();
        ozonService.getData(
                "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
                "350423",
                "27013136-1713353681106@advertising.performance.ozon.ru",
                "w8jTBuPxzAr5iW2dvioeroGh_7aDVHOyS8LhwD4lzK2x5kUQeYytrJ7HeD4yEygPU2iAO9AaU-XOdV7Z1Q",
                2024, 1);
        finish = System.currentTimeMillis();
        timeElapsed = finish - start;
        System.out.println(timeElapsed);
    }

    @Test
    public void generateExcel() throws IOException {
        List<OZON_TableRow> data = ozonService.mergeMapsToTableRows(
                "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
                "350423",
                "27013136-1713353681106@advertising.performance.ozon.ru",
                "w8jTBuPxzAr5iW2dvioeroGh_7aDVHOyS8LhwD4lzK2x5kUQeYytrJ7HeD4yEygPU2iAO9AaU-XOdV7Z1Q",
                2024, 1);
        ExcelBuilder.createDocument(
                ExcelConfig.<OZON_TableRow>builder()
                        .fileName("2024-01.xls")
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
    public void reportCheckerTest(@Autowired ConditionalWithDelayChecker reportChecker) {
        AtomicInteger i = new AtomicInteger();
        reportChecker.start(() -> {
            System.out.println(Instant.now().getEpochSecond());
            i.getAndIncrement();
            return i.get() == 5;
        }, 2L);
        System.out.print(Instant.now().getEpochSecond());
    }
}
