package com.example.consul.mapping;

import com.example.consul.dto.WB.WB_DetailReport;
import com.example.consul.dto.WB.WB_OperationName;
import com.example.consul.dto.WB.WB_SaleReport;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WB_dataProcessing {
    private static String checkOnDouble(@NotNull String sku) {
        int center = sku.length() / 2;
        return sku.substring(0, center)
                .equals(sku.substring(center)) ? sku.substring(center) : sku;
    }

    public static Map<String, List<WB_DetailReport>> groupBySaName(@NotNull List<WB_DetailReport> wbDetailReports) {
        return wbDetailReports.stream()
                .filter(x -> x.getSa_name() != null && !x.getSa_name().isEmpty())
                .peek(x -> {
                    String sku = checkOnDouble(x.getSa_name());
                    x.setSa_name(sku);
                })
                .collect(Collectors.groupingBy(
                        WB_DetailReport::getSa_name,
                        Collectors.toList()
                ));
    }

    public static Map<String, Integer> sumDeliveryAmount(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplier_oper_name()
                                        .equals(WB_OperationName.SALE.toString()))
                                .mapToInt(WB_DetailReport::getQuantity)
                                .sum()));
    }

    public static Map<String, Integer> sumReturnAmount(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplier_oper_name()
                                        .equals(WB_OperationName.RETURN.toString()))
                                .mapToInt(WB_DetailReport::getQuantity)
                                .sum()));
    }

    public static Map<String, Long> getSalesCount(@NotNull List<WB_SaleReport> list) {
        Map<String, List<WB_SaleReport>> groupedMap = list.stream()
                .filter(x -> x.getSupplierArticle() != null && !x.getSupplierArticle().isEmpty())
                .collect(Collectors.groupingBy(
                        WB_SaleReport::getSupplierArticle,
                        Collectors.toList()
                ));

        return groupedMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getOrderType()
                                        .equals(WB_OperationName.SALE_TYPE.toString()))
                                .count()));
    }

    public static Map<String, Double> sumRetail(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplier_oper_name()
                                        .equals(WB_OperationName.SALE.toString()))
                                .mapToDouble(WB_DetailReport::getRetail_amount)
                                .sum()));
    }

    public static Map<String, Double> sumReturn(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplier_oper_name()
                                        .equals(WB_OperationName.RETURN.toString()))
                                .mapToDouble(WB_DetailReport::getRetail_amount)
                                .sum()));
    }

    //поверенный (ПВЗ+эквайринг)
    public static Map<String, Double> sumAttorney(
            @NotNull Map<String,
            @NotNull List<WB_DetailReport>> groupMap,
            @NotNull WB_OperationName operationName
    ) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplier_oper_name()
                                        .equals(operationName.toString()))
                                .mapToDouble(
                                        x -> x.getPpvz_vw() + x.getPpvz_vw_nds()
                                                + x.getAcquiring_fee() + x.getPpvz_reward()
                                )
                                .sum()));
    }

    public static Map<String, Double> sumCommission(
            @NotNull Map<String,
            @NotNull List<WB_DetailReport>> groupMap,
            @NotNull WB_OperationName operationName
    ) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplier_oper_name()
                                        .equals(operationName.toString()))
                                .mapToDouble(
                                        x -> x.getPpvz_vw_nds() + x.getPpvz_for_pay()
                                )
                                .sum()));
    }

    public static Map<String, Double> sumRebill(
            @NotNull Map<String, @NotNull List<WB_DetailReport>> groupMap
    ) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(WB_DetailReport::getRebill_logistic_cost)
                                .sum()));
    }

    public static Map<String, Double> sumAdditional(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplier_oper_name()
                                        .equals(WB_OperationName.RETURN.toString()))
                                .mapToDouble(WB_DetailReport::getAdditional_payment)
                                .sum()));
    }

    public static Map<String, Double> sumLogistic(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplier_oper_name()
                                        .equals(WB_OperationName.LOGISTIC.toString()))
                                .mapToDouble(WB_DetailReport::getDelivery_rub)
                                .sum()));
    }

    //Возврат комиссии, поверенный
    public static Map<String, Double> sumRefundCommission(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplier_oper_name()
                                        .equals(WB_OperationName.RETURN.toString()))
                                .mapToDouble(
                                        x -> x.getPpvz_reward() + x.getAcquiring_fee() +
                                                x.getPpvz_vw_nds() + x.getPpvz_for_pay()
                                )
                                .sum()));
    }

    public static Map<String, Double> sumPenalty(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplier_oper_name()
                                        .equals(WB_OperationName.PENALTY.toString()))
                                .mapToDouble(WB_DetailReport::getPenalty)
                                .sum()));
    }
}
