package com.example.consul.mapping;

import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.dto.OZON.OZON_TransactionReport;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OZON_dataProcessing {

    // Группировка отчета о реализации товаров по артикулу(offer_id)
    static public Map<String, List<OZON_DetailReport.Row>> groupByOfferId(List<OZON_DetailReport.Row> ozonDetailReports) {
        return ozonDetailReports.stream()
                .filter(x -> x.getOffer_id() != null)
                .map(OZON_DetailReport.Row::of)
                .collect(Collectors.groupingBy(OZON_DetailReport.Row::getOffer_id));
    }

    // Суммирование начислений за доставленный товар по артикулу
    static public Map<String,Double> sumSaleForDelivered(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice()*row.getSale_qty())
                                .sum()));
    }

    // Нахождение количества доставленных товаров по артикулу
    static public Map<String,Integer> saleCount(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToInt(OZON_DetailReport.Row::getSale_qty)
                                .sum()));
    }

    // Нахождение количества возвращенных товаров по артикулу
    static public Map<String,Integer> returnCount(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToInt(OZON_DetailReport.Row::getReturn_qty)
                                .sum()));
    }

    // Нахождение суммы возврата товаров по артикулу
    static public Map<String,Double> sumReturn(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice()*row.getReturn_qty())
                                .sum()));
    }

    // Нахождение комиссии за продажу по артикулу
    static public Map<String,Double> sumSalesCommission(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice()*row.getCommission_percent()*(row.getSale_qty()-row.getReturn_qty()))
                                .sum()));
    }

    static public Map<Long, Double> sumAcquiringBySku(List<OZON_TransactionReport.Operation> operations) {
        return operations.stream().collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSku,
                Collectors.summingDouble(OZON_TransactionReport.Operation::getPrice)));
    }
}
