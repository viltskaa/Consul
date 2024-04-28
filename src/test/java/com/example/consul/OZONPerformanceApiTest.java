package com.example.consul;

import com.example.consul.dto.OZON.OZON_PerformanceCampaigns;
import com.example.consul.dto.OZON.OZON_PerformanceReport;
import com.example.consul.dto.OZON.OZON_PerformanceReportStatus;
import com.example.consul.dto.OZON.OZON_PerformanceStatistic;
import com.example.consul.mapping.OZON_dataProcessing;
import com.example.consul.services.OZON_Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@SpringBootTest
public class OZONPerformanceApiTest {
    @Autowired
    private OZON_Service ozonService;

    @Test
    public void getCampaignsTest() throws InterruptedException {
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

        AtomicReference<OZON_PerformanceReportStatus> status = new AtomicReference<>();
        do {
            status.set(ozonService.getPerformanceReportStatusByUUID(
                    clientId,
                    statistic.getUUID()
            ));
            Thread.sleep(5000);
        } while (!status.get().getState().equals(OZON_PerformanceReportStatus.State.OK));

        List<OZON_PerformanceReport> reports = ozonService.getPerformanceReportByUUID(
                clientId,
                statistic.getUUID()
        );
        Map<String, Double> values = reports.stream()
                .map(OZON_PerformanceReport::getReport)
                .map(OZON_PerformanceReport.Report::getRows)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(OZON_PerformanceReport.Product::getSku))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        value ->
                                value.getValue().stream()
                                        .map(OZON_PerformanceReport.Product::getMoneySpent)
                                        .map(x -> x.replace(",", "."))
                                        .mapToDouble(Double::parseDouble)
                                        .sum()
                ));

        ozonService.setHeader("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        Map<String, List<Long>> offerSku = ozonService.getProductInfoByOfferId(
                ozonService.getListOfferIdByDate("2024-01"))
                .getSkuListByOfferId();

        Map<String, Double> valuesWithOfferId = OZON_dataProcessing
                .sumStencilByOfferId(values, offerSku);
        System.out.println(valuesWithOfferId);
    }
}
