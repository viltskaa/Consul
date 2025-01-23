package com.example.consul.mapping;

import com.example.consul.dto.WB.WB_DetailReport;
import com.example.consul.dto.WB.WB_OperationName;
import com.example.consul.dto.WB.WB_SaleReport;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class WB_dataProcessing {
    // Избавление от дублей
    private static String checkOnDouble(@NotNull String sku) {
        int center = sku.length() / 2;
        return sku.substring(0, center)
                .equals(sku.substring(center)) ? sku.substring(center) : sku;
    }

    // Собираем все ключи для будущего HashMap. Ключи - артикулы товаров.
    public static List<String> getKeys(@NotNull List<WB_DetailReport> wbDetailReports){
        return wbDetailReports.stream()
                .filter(x -> !x.getSaName().isEmpty())
                .peek(x -> {
                    String sku = checkOnDouble(x.getSaName());
                    x.setSaName(sku);
                })
                .map(WB_DetailReport::getSaName)
                .collect(Collectors.toList());
    }

    public static Map<String, List<WB_DetailReport>> groupBySaName(@NotNull List<WB_DetailReport> wbDetailReports) {
        return wbDetailReports.stream()
                .filter(x -> x.getSaName() != null && !x.getSaName().isEmpty())
                .peek(x -> {
                    String sku = checkOnDouble(x.getSaName());
                    x.setSaName(sku);
                })
                .collect(Collectors.groupingBy(
                        WB_DetailReport::getSaName,
                        Collectors.toList()
                ));
    }

    public static Map<String, Integer> sumDeliveryAmount(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplierOperName()
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
                                .filter(report -> report.getSupplierOperName()
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
                                .filter(report -> report.getSupplierOperName()
                                        .equals(WB_OperationName.SALE.toString()))
                                .mapToDouble(WB_DetailReport::getRetailAmount)
                                .sum()));
    }

    public static Map<String, Double> sumReturn(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplierOperName()
                                        .equals(WB_OperationName.RETURN.toString()))
                                .mapToDouble(WB_DetailReport::getRetailAmount)
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
                                .filter(report -> report.getSupplierOperName()
                                        .equals(operationName.toString()))
                                .mapToDouble(
                                        x -> x.getPpvzVw() + x.getPpvzVwNds()
                                                + x.getAcquiringFee() + x.getPpvzReward()
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
                                .filter(report -> report.getSupplierOperName()
                                        .equals(operationName.toString()))
                                .mapToDouble(
                                        x -> x.getPpvzVwNds() + x.getPpvzForPay()
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
                                .mapToDouble(WB_DetailReport::getRebillLogisticCost)
                                .sum()));
    }

    public static Map<String, Double> sumAdditional(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplierOperName()
                                        .equals(WB_OperationName.RETURN.toString()))
                                .mapToDouble(WB_DetailReport::getAdditionalPayment)
                                .sum()));
    }

    public static Map<String, Double> sumLogistic(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplierOperName()
                                        .equals(WB_OperationName.LOGISTIC.toString()))
                                .mapToDouble(WB_DetailReport::getDeliveryRub)
                                .sum()));
    }

    //Возврат комиссии, поверенный
    public static Map<String, Double> sumRefundCommission(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplierOperName()
                                        .equals(WB_OperationName.RETURN.toString()))
                                .mapToDouble(
                                        x -> x.getPpvzReward() + x.getAcquiringFee() +
                                                x.getPpvzVwNds() + x.getPpvzVw()
                                )
                                .sum()));
    }

    public static Map<String, Double> sumPenalty(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplierOperName()
                                        .equals(WB_OperationName.PENALTY.toString()))
                                .mapToDouble(WB_DetailReport::getPenalty).sum()
                ));
    }

    public static Map<String, Double> sumCompensationReplaced(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplierOperName()
                                        .equals(WB_OperationName.COMPENSATION_REPlACED.toString()))
                                .mapToDouble(WB_DetailReport::getPpvzForPay).sum()
                ));
    }

    public static Map<String, Double> sumCompensationLosted(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplierOperName()
                                        .equals(WB_OperationName.COMPENSATION_LOSTED.toString()))
                                .mapToDouble(WB_DetailReport::getPpvzForPay).sum()
                ));
    }

    public static Map<String, Double> sumCompensationDefected(@NotNull Map<String, List<WB_DetailReport>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getSupplierOperName()
                                        .equals(WB_OperationName.COMPENSATION_DEFECT.toString()))
                                .mapToDouble(WB_DetailReport::getPpvzForPay).sum()
                ));
    }

    public static Double sumDoubleValuesByConditions(@NotNull List<WB_DetailReport> values,
                                               Predicate<WB_DetailReport> predicate,
                                               ToDoubleFunction<WB_DetailReport> supplier) {
        return values.stream().filter(predicate).mapToDouble(supplier).sum();
    }
}
