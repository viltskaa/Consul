package com.example.consul.mapping;

import com.example.consul.dto.OZON_DetailReport;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OZON_dataProcessing {

    public OZON_dataProcessing() {
    }

    public Map<String, List<OZON_DetailReport.Row>> groupByOfferId(List<OZON_DetailReport.Row> ozonDetailReports) {
        return ozonDetailReports.stream()
                .filter(x -> x.getOffer_id() != null)
                .map(OZON_DetailReport.Row::of)
                .collect(Collectors.groupingBy(OZON_DetailReport.Row::getOffer_id));
    }

    public Map<String,Double> sumSaleForDelivered(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice()*row.getSale_qty())
                                .sum()));
    }

    public Map<String,Integer> sumSaleCount(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToInt(OZON_DetailReport.Row::getSale_qty)
                                .sum()));
    }

    public Map<String,Integer> sumReturnCount(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToInt(OZON_DetailReport.Row::getReturn_qty)
                                .sum()));
    }

    public Map<String,Double> sumReturn(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice()*row.getReturn_qty())
                                .sum()));
    }

    public Map<String,Double> sumSalesCommission(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice()*row.getCommission_percent()*(row.getSale_qty()-row.getReturn_qty()))
                                .sum()));
    }
}
