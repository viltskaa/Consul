package com.example.consul.mapping;

import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.dto.OZON.OZON_PerformanceReport;
import com.example.consul.dto.OZON.OZON_TransactionReport;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OZON_dataProcessing {

    /**
     * Группировка отчета о реализации товаров по артикулу(offer_id)
     *
     * @param ozonDetailReports таблица отчета (rows из result в OZON_DetailReport)
     * @return Map [offer_id, (rows с этим offer_id)]
     */
    static public Map<String, List<OZON_DetailReport.Row>> groupByOfferId(List<OZON_DetailReport.Row> ozonDetailReports) {
        return ozonDetailReports.stream()
                .filter(x -> x.getOffer_id() != null)
                .map(OZON_DetailReport.Row::of)
                .collect(Collectors.groupingBy(OZON_DetailReport.Row::getOffer_id));
    }

    /**
     * Суммирование начислений за доставленный товар по артикулу
     *
     * @param groupMap Map [offer_id, (rows с этим offer_id)]
     * @return Map [offer_id, (начисление за доставленный товар)]
     */
    static public Map<String, Double> sumSaleForDelivered(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice() * row.getSale_qty())
                                .sum()));
    }


    /**
     * Нахождение количества доставленных товаров по артикулу
     *
     * @param groupMap Map [offer_id, (rows с этим offer_id)]
     * @return Map [offer_id, (доставлено для этого товара)]
     */
    static public Map<String, Integer> saleCount(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToInt(OZON_DetailReport.Row::getSale_qty)
                                .sum()));
    }

    /**
     * Нахождение количества возвращенных товаров по артикулу
     *
     * @param groupMap  Map [offer_id, (rows с этим offer_id)]
     * @return Map [offer_id, (возвращено для этого товара)]
     */
    static public Map<String, Integer> returnCount(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToInt(OZON_DetailReport.Row::getReturn_qty)
                                .sum()));
    }

    /**
     * Нахождение суммы возврата товаров по артикулу
     *
     * @param groupMap Map [offer_id, (rows с этим offer_id)]
     * @return Map [offer_id, (сумма возврата для этого товара)]
     */
    static public Map<String, Double> sumReturn(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice() * row.getReturn_qty())
                                .sum()));
    }

    /**
     * Нахождение комиссии за продажу по артикулу
     *
     * @param groupMap Map [offer_id, (rows с этим offer_id)]
     * @return Map [offer_id, (комиссия за продажу для этого товара)]
     */
    static public Map<String, Double> sumSalesCommission(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice() * row.getCommission_percent() * (row.getSale_qty() - row.getReturn_qty()))
                                .sum()));
    }

    /**
     * Нахождение эквайринга по артикулу
     *
     * @param offerSku Map [offer_id, (sku этого offer_id)]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (эквайринг для этого товара)]
     */
    static public Map<String, Double> sumAcquiring(Map<String, List<Long>> offerSku, List<OZON_TransactionReport.Operation> operations) {
        return offerSku.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                        .mapToDouble(row -> operations.stream()
                                .filter(op -> op.hasSkus(entry.getValue())
                                        && op.getPriceByServiceName("MarketplaceRedistributionOfAcquiringOperation") != null)
                                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSku,
                                        Collectors.summingDouble(OZON_TransactionReport.Operation::getPrice)))
                                .entrySet().stream().filter(o -> o.getKey().equals(row))
                                .mapToDouble(Map.Entry::getValue).sum())
                        .sum()
        ));
    }

    // Нахождение последней мили по артикулу
    /**
     * Нахождение последней мили по артикулу
     *
     * @param offerSku Map [offer_id, (sku этого offer_id)]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (последняя для этого товара)]
     */
    static public Map<String, Double> sumLastMile(Map<String, List<Long>> offerSku, List<OZON_TransactionReport.Operation> operations) {
        Map<String, List<OZON_TransactionReport.Operation>> groupByPostingNumber = operations
                .stream()
                .filter(x -> x.getPosting() != null)
                .map(OZON_TransactionReport.Operation::of)
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getPostingNumber));

        return offerSku.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> groupByPostingNumber.entrySet().stream()
                                .filter(groupEntry -> groupEntry.getValue().stream()
                                        .anyMatch(op -> op.hasSkus(entry.getValue())))
                                .flatMap(groupEntry -> groupEntry.getValue().stream())
                                .filter(s -> s.getServices() != null && s.getServices().stream()
                                        .anyMatch(x -> "MarketplaceServiceItemDelivToCustomer".equals(x.getName())))
                                .mapToDouble(x -> x.getServices().stream()
                                        .filter(y -> "MarketplaceServiceItemDelivToCustomer".equals(y.getName()))
                                        .mapToDouble(OZON_TransactionReport.Service::getPrice)
                                        .sum())
                                .sum()
                ));
    }

    /**
     * Нахождение логистики по артикулу
     *
     * @param offerSku Map [offer_id, (sku этого offer_id)]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (последняя для этого товара)]
     */
    static public Map<String, Double> sumLogistic(Map<String, List<Long>> offerSku, List<OZON_TransactionReport.Operation> operations) {
        return offerSku.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> operations.stream()
                                .filter(op -> op.hasSkus(entry.getValue()))
                                .filter(s -> s.getServices() != null && s.getServices().stream()
                                        .anyMatch(x -> "MarketplaceServiceItemDirectFlowLogistic".equals(x.getName()) || "MarketplaceServiceItemDirectFlowLogisticVDC".equals(x.getName())))
                                .mapToDouble(x -> x.getServices().stream()
                                        .filter(y -> "MarketplaceServiceItemDirectFlowLogistic".equals(y.getName()) || "MarketplaceServiceItemDirectFlowLogisticVDC".equals(y.getName()))
                                        .mapToDouble(OZON_TransactionReport.Service::getPrice)
                                        .sum())
                                .sum()
                ));
    }


    /**
     * Нахождение обработки отправления по артикулу
     *
     * @param offerSku Map [offer_id, (sku этого offer_id)]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (обработка отправления для этого товара)]
     */
    static public Map<String, Double> sumShipmentProcessing(Map<String, List<Long>> offerSku, List<OZON_TransactionReport.Operation> operations) {
        return offerSku.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> operations.stream()
                                .filter(op -> op.hasSkus(entry.getValue()))
                                .filter(s -> s.getServices() != null && s.getServices().stream()
                                        .anyMatch(x -> "MarketplaceServiceItemDropoffSC".equals(x.getName()) || "MarketplaceServiceItemDropoffPVZ".equals(x.getName())))
                                .mapToDouble(x -> x.getServices().stream()
                                        .filter(y -> "MarketplaceServiceItemDropoffSC".equals(y.getName()) || "MarketplaceServiceItemDropoffPVZ".equals(y.getName()))
                                        .mapToDouble(OZON_TransactionReport.Service::getPrice)
                                        .sum())
                                .sum()
                ));
    }

    /**
     * Нахождение обработки возврата по артикулу
     *
     * @param offerSku Map [offer_id, (sku этого offer_id)]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (обработка отправления для этого товара)]
     */
    static public Map<String, Double> sumReturnProcessing(Map<String, List<Long>> offerSku, List<OZON_TransactionReport.Operation> operations) {
        return offerSku.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> operations.stream()
                                .filter(op -> op.hasSkus(entry.getValue()))
                                .filter(s -> s.getServices() != null && s.getServices().stream()
                                        .anyMatch(x -> "MarketplaceServiceItemRedistributionReturnsPVZ".equals(x.getName()) || "MarketplaceServiceItemReturnNotDelivToCustomer".equals(x.getName()) || "MarketplaceServiceItemReturnPartGoodsCustomer".equals(x.getName())))
                                .mapToDouble(x -> x.getServices().stream()
                                        .filter(y -> "MarketplaceServiceItemRedistributionReturnsPVZ".equals(y.getName()) || "MarketplaceServiceItemReturnNotDelivToCustomer".equals(y.getName()) || "MarketplaceServiceItemReturnPartGoodsCustomer".equals(y.getName()))
                                        .mapToDouble(OZON_TransactionReport.Service::getPrice)
                                        .sum())
                                .sum()
                ));
    }

    /**
     * Нахождение доставки возврата по артикулу
     *
     * @param offerSku Map [offer_id, (sku этого offer_id)]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (доставка возврата для этого товара)]
     */
    static public Map<String, Double> sumReturnDelivery(Map<String, List<Long>> offerSku, List<OZON_TransactionReport.Operation> operations) {
        return offerSku.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> operations.stream()
                                .filter(op -> op.hasSkus(entry.getValue()) && !op.checkServiceName("MarketplaceServiceItemDropoffSC") && !op.checkServiceName("MarketplaceServiceItemDropoffPVZ"))
                                .filter(s -> s.getServices() != null && s.getServices().stream()
                                        .anyMatch(x -> "MarketplaceServiceItemReturnFlowLogistic".equals(x.getName()) || "MarketplaceServiceItemDirectFlowLogistic".equals(x.getName())))
                                .mapToDouble(x -> x.getServices().stream()
                                        .filter(y -> "MarketplaceServiceItemReturnFlowLogistic".equals(y.getName()) || "MarketplaceServiceItemDirectFlowLogistic".equals(y.getName()))
                                        .mapToDouble(OZON_TransactionReport.Service::getPrice)
                                        .sum())
                                .sum()
                ));
    }

    /**
     * Нахождение трафарета(реклама) по sku
     *
     * @param reports лист OZON_PerformanceReport
     * @return Map [sku, (сумма этого sku)]
     */
    static public Map<String, Double> sumStencilBySku(List<OZON_PerformanceReport> reports) {
        return reports.stream()
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
    }

    /**
     * Нахождение трафарета(реклама) по sku
     *
     * @param stencilsBySku Map [sku, (сумма этого sku)]
     * @param offerSku Map [offer_id, (sku этого offer_id)]
     * @return Map [offer_id, (трафарет для этого offer_id)]
     */
    static public Map<String, Double> sumStencilByOfferId(@NotNull Map<String, Double> stencilsBySku,
                                                          @NotNull Map<String, List<Long>> offerSku) {
        return offerSku.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> stencilsBySku.entrySet().stream()
                                .filter(x -> entry.getValue().contains(Long.valueOf(x.getKey())))
                                .mapToDouble(Map.Entry::getValue)
                                .sum()
                ));
    }
}
