package com.example.consul.mapping;

import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.dto.WB.WB_DetailReport;
import com.example.consul.dto.WB.WB_DetailReportShort;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class WB_dataProcessing {
    public static Map<String, List<WB_DetailReport>> groupBySaName(List<WB_DetailReport> wbDetailReports) {
        return wbDetailReports.stream()
                .collect(Collectors.groupingBy(
                        x -> x.getSa_name() != null ? x.getSa_name() : "NULL",
                        Collectors.toList()
                ));
    }

    public static Map<String, Integer> sumCountSale(Map<String, List<WB_DetailReport>> groupMap){
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> Objects.equals(report.getDoc_type_name(), "Продажа"))
                                .mapToInt(WB_DetailReport::getQuantity)
                                .sum()));
    }

    public static Map<String, Double> sumAcquiring(Map<String, List<WB_DetailReport>> groupMap){
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> Objects.equals(report.getDoc_type_name(), "Продажа"))
                                .mapToDouble(WB_DetailReport::getAcquiring_fee)
                                .sum()));
    }

    // todo Суммация WB_AdReport по цене, с фильтром "!= пробный"
    // todo Собрать детализацию в таблицу
}
