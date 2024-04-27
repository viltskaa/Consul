package com.example.consul;

import com.example.consul.dto.OZON.OZON_PerformanceCampaigns;
import com.example.consul.dto.OZON.OZON_PerformanceReport;
import com.example.consul.dto.OZON.OZON_PerformanceStatistic;
import com.example.consul.services.OZON_Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class OZONPerformanceApiTest {
    @Autowired
    private OZON_Service ozonService;

    @Test
    public void getCampaignsTest() {
        String clientId = "27013136-1713353681106@advertising.performance.ozon.ru";


        ozonService.getPerformanceToken(
                clientId,
                "w8jTBuPxzAr5iW2dvioeroGh_7aDVHOyS8LhwD4lzK2x5kUQeYytrJ7HeD4yEygPU2iAO9AaU-XOdV7Z1Q"
        );

        String dateFrom = "2024-01-01";
        String dateTo = "2024-01-31";
        OZON_PerformanceCampaigns ozonPerformanceCampaigns = ozonService.getCampaigns(
                clientId,
                dateFrom,
                dateTo
        );

        List<String> activeCampaignList = ozonPerformanceCampaigns.getRows()
                .stream().filter(x -> x.getStatus().equals("running"))
                .map(OZON_PerformanceCampaigns.OZON_PerformanceCampaign::getId).toList();

        OZON_PerformanceStatistic statistic = ozonService.getPerformanceStatisticByCampaignId(
                clientId,
                activeCampaignList,
                dateFrom,
                dateTo
        );
        List<OZON_PerformanceReport> reports = ozonService.getPerformanceReportByUUID(
                clientId,
                statistic.getUUID()
        );
        System.out.println(reports);
    }
}
