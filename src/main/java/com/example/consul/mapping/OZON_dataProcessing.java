package com.example.consul.mapping;

import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.dto.OZON.OZON_TransactionReport;
import com.example.consul.services.OZON_Service;

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
    static public Map<String, Double> sumSaleForDelivered(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice() * row.getSale_qty())
                                .sum()));
    }

    // Нахождение количества доставленных товаров по артикулу
    static public Map<String, Integer> saleCount(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToInt(OZON_DetailReport.Row::getSale_qty)
                                .sum()));
    }

    // Нахождение количества возвращенных товаров по артикулу
    static public Map<String, Integer> returnCount(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToInt(OZON_DetailReport.Row::getReturn_qty)
                                .sum()));
    }

    // Нахождение суммы возврата товаров по артикулу
    static public Map<String, Double> sumReturn(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice() * row.getReturn_qty())
                                .sum()));
    }

    // Нахождение комиссии за продажу по артикулу
    static public Map<String, Double> sumSalesCommission(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(row -> row.getPrice() * row.getCommission_percent() * (row.getSale_qty() - row.getReturn_qty()))
                                .sum()));
    }

    // Нахождение эквайринга по артикулу
    static public Map<String, Double> sumAcquiring(Map<String, List<Long>> offerSku, List<OZON_TransactionReport.Operation> operations) {
        Map<Long, Double> skuPrice = operations.stream().collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSku,
                Collectors.summingDouble(OZON_TransactionReport.Operation::getPrice)));

        return offerSku.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                        .mapToDouble(row -> skuPrice.entrySet().stream().filter(o -> o.getKey().equals(row))
                                .mapToDouble(Map.Entry::getValue).sum())
                        .sum()
        ));
    }

    //Получить список sku для каждого артикула
    static public Map<String, List<Long>> getOfferSku(OZON_Service ozonService, String date) {
        return ozonService
                .getProductInfoByOfferId(OZON_dataProcessing
                        .groupByOfferId(ozonService.getDetailReport(date)
                                .getResult().getRows())
                        .keySet()
                        .toArray(new String[0]))
                .getSkuListByOfferId();
    }

    // Нахождение последней мили по артикулу
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

    // Нахождение логистики по артикулу
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

    // Нахождение обработки отправления по артикулу
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

    // Нахождение обработки возврата по артикулу
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

    // Нахождение доставки возврата по артикулу
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
}
