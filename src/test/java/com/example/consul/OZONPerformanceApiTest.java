package com.example.consul;

import com.example.consul.dto.OZON.OZON_PerformanceCampaigns;
import com.example.consul.dto.OZON.OZON_campaignProductsInfo;
import com.example.consul.services.OZON_Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OZONPerformanceApiTest {
    private final OZON_Service ozonService;

    public OZONPerformanceApiTest(OZON_Service ozonService) {
        this.ozonService = ozonService;
    }

    @Test
    public void getTokenTest() {
        String token = ozonService.getPerformanceToken(
                "27013136-1713353681106@advertising.performance.ozon.ru",
                "w8jTBuPxzAr5iW2dvioeroGh_7aDVHOyS8LhwD4lzK2x5kUQeYytrJ7HeD4yEygPU2iAO9AaU-XOdV7Z1Q"
        );
        System.out.println(token);
    }

    @Test
    public void getCampaignsTest() {
        String token = ozonService.getPerformanceToken(
                "27013136-1713353681106@advertising.performance.ozon.ru",
                "w8jTBuPxzAr5iW2dvioeroGh_7aDVHOyS8LhwD4lzK2x5kUQeYytrJ7HeD4yEygPU2iAO9AaU-XOdV7Z1Q"
        );

        OZON_PerformanceCampaigns ozonPerformanceCampaigns = ozonService.getCampaigns(
                "27013136-1713353681106@advertising.performance.ozon.ru",
                "2024-01-01",
                "2024-01-31"
        );
        System.out.println();
    }
}
