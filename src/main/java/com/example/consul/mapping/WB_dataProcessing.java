package com.example.consul.mapping;

import com.example.consul.dto.WB_DetailReportShort;
import com.example.consul.services.WB_Service;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WB_dataProcessing {
    private final WB_Service wbService;

    public WB_dataProcessing(WB_Service wbService) {
        this.wbService = wbService;
        wbService.setApiKey("eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTcyNzgxMzkyMSwiaWQiOiIwYTY5NDVkZS0wODQyLTQ1ZmItOGEyMC0zNTMzNzliYjk1NjUiLCJpaWQiOjQ1ODkwNDkwLCJvaWQiOjg5NzE2NiwicyI6MTA3Mzc0MjMzNCwic2lkIjoiMTZhMGZiZWEtYWVmZi00YjgxLThmNzEtZjYyZDlhYjJmMGM1IiwidCI6ZmFsc2UsInVpZCI6NDU4OTA0OTB9.DtgYFO1TioCOeKiKI4VKw0_QbD8S4908JSxj2196k_pUDH1vgNBiUPImwWMGhaDgpE8GVEdzOQnPj23aiRT4JQ");
    }

    public Map<String, List<WB_DetailReportShort>> groupBy_sa_name(@NotNull String dateFrom,
                                                                   @NotNull String dateTo){
        return wbService.getDetailReport(dateFrom,dateTo).stream().filter(x -> !x.getSa_name().isEmpty()).map(WB_DetailReportShort::of)
                .collect(Collectors.groupingBy(WB_DetailReportShort::getSa_name));
    }
}
