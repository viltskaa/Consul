package com.example.consul.mapping;

import com.example.consul.dto.OZON.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class OZON_dataProcessing {

    static public List<Long> getSkus(List<OZON_TransactionReport.Operation> operations) {
        return operations.stream().
                flatMap(operation -> operation.getItems().stream())
                .map(OZON_TransactionReport.Item::getSku)
                .collect(Collectors.toList());
    }

    static public Map<String, Long> getOfferSku(List<OZON_SkuProductsReport.OZON_SkuProduct> products) {
        return products.stream().collect(
                Collectors.toMap(
                        OZON_SkuProductsReport.OZON_SkuProduct::getOffer_id,
                        OZON_SkuProductsReport.OZON_SkuProduct::getSku
                )
        );
    }

    /**
     * Общий метод приведения в итоговую map, для тех случаев, когда смотрим на сервисы в транзакциях
     *
     * @param collect
     * @param skuOffer
     * @return
     */
    static public Map<String, Double> toOfferPrice(Map<Long, Double> collect, Map<Long, String> skuOffer) {
        Map<String, Double> res = skuOffer.entrySet().stream()
                .filter(entry -> collect.containsKey(entry.getKey())) // фильтруем, если есть ключ в collect
                .collect(Collectors.toMap(
                        Map.Entry::getValue,               // Используем offer_id как ключ
                        entry -> collect.get(entry.getKey()) // Используем значение из collect
                ));

        res.put("none", collect.entrySet().stream()
                .filter(entry -> !skuOffer.containsKey(entry.getKey()))
                .mapToDouble(Map.Entry::getValue)
                .sum());

        return res;
    }

    /**
     * Группировка отчета о реализации товаров по артикулу(offer_id)
     *
     * @param ozonDetailReports таблица отчета (rows из result в OZON_DetailReport)F
     * @return Map [offer_id, (rows с этим offer_id)]
     */
    static public Map<String, List<OZON_DetailReport.Row>> groupByOfferId(List<OZON_DetailReport.Row> ozonDetailReports) {
        return ozonDetailReports.stream()
                .filter(x -> x.getItem() != null)
                .collect(Collectors.groupingBy(row -> row.getItem().getOfferId()));
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
                                .filter(row -> row.getDeliveryCommission() != null)
                                .mapToDouble(row -> row.getSellerPricePerInstance() * row.getDeliveryCommission().getQuantity())
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
                                .filter(row -> row.getDeliveryCommission() != null)
                                .mapToInt(row -> row.getDeliveryCommission().getQuantity())
                                .sum()));
    }

    /**
     * Нахождение количества возвращенных товаров по артикулу
     *
     * @param groupMap Map [offer_id, (rows с этим offer_id)]
     * @return Map [offer_id, (возвращено для этого товара)]
     */
    static public Map<String, Integer> returnCount(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        return groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(row -> row.getReturnCommission() != null)
                                .mapToInt(row -> row.getReturnCommission().getQuantity())
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
                                .filter(row -> row.getReturnCommission() != null)
                                .mapToDouble(row -> row.getSellerPricePerInstance() * row.getReturnCommission().getQuantity())
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
                                .mapToDouble(row -> row.getSellerPricePerInstance() * row.getCommissionRatio() * ((row.getDeliveryCommission() == null ? 0 : row.getDeliveryCommission().getQuantity()) - (row.getReturnCommission() == null ? 0 : row.getReturnCommission().getQuantity())))
                                .sum()));
    }


    /**
     * Нахождение Ozon Премиум
     *
     * @param operations список операций ( OZON_TransactionReport => result => operations )
     * @return Общая сумма Ozon Premium
     */
    static public Double sumOzonPremium(List<OZON_TransactionReport.Operation> operations) {
        return operations.stream()
                .filter(op -> Objects.equals(op.getOperation_type(), "OperationMarketplacePremiumSubscribtion"))
                .mapToDouble(OZON_TransactionReport.Operation::getAmount)
                .sum();
    }

    /**
     * Нахождение Услуг продвижения товаров
     *
     * @param operations список операций ( OZON_TransactionReport => result => operations )
     * @return Общая сумма Услуг продвижения товаров
     */
    static public Double sumActionCost(List<OZON_TransactionReport.Operation> operations) {
        return operations.stream()
                .filter(op -> Objects.equals(op.getOperation_type(), "MarketplaceMarketingActionCostOperation"))
                .mapToDouble(OZON_TransactionReport.Operation::getAmount)
                .sum();
    }


    /**
     * Нахождение эквайринга по артикулу
     *
     * @param skuOffer   Map [offer_id, sku этого offer_id]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (эквайринг для этого товара)]
     */
    static public Map<String, Double> sumAcquiring(Map<Long, String> skuOffer, List<OZON_TransactionReport.Operation> operations) {
        Map<Long, Double> collect = operations.stream()
                .filter(operation -> operation.checkServiceName("MarketplaceRedistributionOfAcquiringOperation"))
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceRedistributionOfAcquiringOperation"))));

//        return skuOffer.entrySet().stream()
//                .filter(entry -> collect.containsKey(entry.getKey())) // фильтруем, если есть ключ в collect
//                .collect(Collectors.toMap(
//                        Map.Entry::getValue,               // Используем offer_id как ключ
//                        entry -> collect.get(entry.getKey()) // Используем значение из collect
//                ));
        return toOfferPrice(collect, skuOffer);
    }

    /**
     * Нахождение Ozon Рассрочки по артикулу
     *
     * @param skuOffer   Map [offer_id, (sku этого offer_id)]
     * @param operations список операций ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (Ozon Рассрочка для товара)]
     */
    static public Map<String, Double> sumInstallments(Map<Long, String> skuOffer, List<OZON_TransactionReport.Operation> operations) {
        Map<Long, Double> collect = operations.stream()
                .filter(operation -> operation.checkServiceName("MarketplaceServiceItemInstallment"))
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceServiceItemInstallment"))));

        return toOfferPrice(collect, skuOffer);
    }


    /**
     * Нахождение последней мили по артикулу
     *
     * @param skuOffer   Map [offer_id, (sku этого offer_id)]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (последняя для этого товара)]
     */
    static public Map<String, Double> sumLastMile(Map<Long, String> skuOffer, List<OZON_TransactionReport.Operation> operations) {
        Map<Long, Double> collect = operations.stream()
                .filter(operation -> operation.checkServiceName("MarketplaceServiceItemDelivToCustomer"))
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceServiceItemDelivToCustomer"))));

        return toOfferPrice(collect, skuOffer);
    }

    static public Map<String, Double> sumLastMile_(Map<Long, String> skuOffer, List<OZON_TransactionReport.Operation> operations) {
//        Map<String, List<OZON_TransactionReport.Operation>> groupByPostingNumber = operations
//                .stream()
//                .filter(x -> x.getPosting() != null)
//                .map(OZON_TransactionReport.Operation::of)
//                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getPostingNumber));
//
////        groupByPostingNumber.forEach((k, v) -> System.out.println(k + " : " + v.size()));
//
//        Map<Long, Double> collect = operations.stream()
//                .filter(operation -> operation.checkServiceName("MarketplaceServiceItemDelivToCustomer")
//                        && operation.hasPostingNumber() &&
//                        (operation.getOperation_type().equals("OperationAgentDeliveredToCustomer")
//                                || operation.getOperation_type().equals("OperationAgentStornoDeliveredToCustomer")
////                        || operation.getOperation_type().equals("OperationReturnGoodsFBSofRMS")
//                        ))
//                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
//                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceServiceItemDelivToCustomer"))));
//
//        Map<String, Double> res = skuOffer.entrySet().stream()
//                .filter(entry -> collect.containsKey(entry.getValue())) // фильтруем, если есть ключ в collect
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,               // Используем offer_id как ключ
//                        entry -> collect.get(entry.getValue()) // Используем значение из collect
//                ));
//        res.put("none", operations.stream()
//                .filter(operation -> operation.getItems().isEmpty())
//                .mapToDouble(item -> item.getPriceByServiceNameNoNull("MarketplaceServiceItemDelivToCustomer")).sum());
        return new HashMap<>();
    }

    /**
     * Нахождение логистики по артикулу
     *
     * @param skuOffer   Map [offer_id, (sku этого offer_id)]
     * @param operations список операций ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (сумма для этого артикля)]
     */
    static public Map<String, Double> sumLogistic(Map<Long, String> skuOffer, List<OZON_TransactionReport.Operation> operations) {
        Map<Long, Double> collect = operations.stream()
                .filter(operation -> operation.checkServiceName("MarketplaceServiceItemDirectFlowLogistic")
                        || operation.checkServiceName("MarketplaceServiceItemDirectFlowLogisticVDC"))
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceServiceItemDirectFlowLogistic")
                                + val.getPriceByServiceNameNoNull("MarketplaceServiceItemDirectFlowLogisticVDC"))));

        return toOfferPrice(collect, skuOffer);
    }

    /**
     * Нахождение услуга продвижения «Бонусы продавца» по артикулу
     *
     * @param skuOffer   Map [offer_id, (sku этого offer_id)]
     * @param operations список операций ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (сумма для этого артикля)]
     */
    static public Map<String, Double> sumCashbackIndividualPoints(Map<Long, String> skuOffer, List<OZON_TransactionReport.Operation> operations) {
        Map<Long, Double> collect = operations.stream()
                .filter(operation -> operation.checkServiceName("MarketplaceServicePremiumCashbackIndividualPoints"))
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceServicePremiumCashbackIndividualPoints"))));

        return toOfferPrice(collect, skuOffer);
    }

    /**
     * Нахождение обработки отправления по артикулу
     *
     * @param skuOffer   Map [offer_id, (sku этого offer_id)]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (обработка отправления для этого товара)]
     */
    static public Map<String, Double> sumShipmentProcessing(Map<Long, String> skuOffer, List<OZON_TransactionReport.Operation> operations) {
        Map<Long, Double> collect = operations.stream()
                .filter(operation -> operation.checkServiceName("MarketplaceServiceItemDropoffSC")
                        || operation.checkServiceName("MarketplaceServiceItemDropoffPVZ"))
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceServiceItemDropoffSC")
                                + val.getPriceByServiceNameNoNull("MarketplaceServiceItemDropoffPVZ")
                        )));

        return toOfferPrice(collect, skuOffer);
    }

    /**
     * Нахождение обработки возврата по артикулу
     *
     * @param skuOffer   Map [offer_id, (sku этого offer_id)]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (обработка отправления для этого товара)]
     */
    static public Map<String, Double> sumReturnProcessing(Map<Long, String> skuOffer, List<OZON_TransactionReport.Operation> operations) {
        Map<Long, Double> collect = operations.stream()
                .filter(operation -> operation.checkServiceName("MarketplaceServiceItemRedistributionReturnsPVZ")
                        || operation.checkServiceName("MarketplaceServiceItemReturnNotDelivToCustomer")
                        || operation.checkServiceName("MarketplaceServiceItemReturnPartGoodsCustomer"))
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceServiceItemRedistributionReturnsPVZ")
                                + val.getPriceByServiceNameNoNull("MarketplaceServiceItemReturnNotDelivToCustomer")
                                + val.getPriceByServiceNameNoNull("MarketplaceServiceItemReturnPartGoodsCustomer")
                        )));

        return toOfferPrice(collect, skuOffer);
    }

    /**
     * Нахождение доставки возврата по артикулу
     *
     * @param skuOffer   Map [offer_id, (sku этого offer_id)]
     * @param operations список операци ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (доставка возврата для этого товара)]
     */
    static public Map<String, Double> sumReturnDelivery(Map<Long, String> skuOffer, List<OZON_TransactionReport.Operation> operations) {
        Map<Long, Double> collect = operations.stream()
                .filter(operation -> (operation.checkServiceName("MarketplaceServiceItemReturnFlowLogistic")
                        || operation.checkServiceName("MarketplaceServiceItemDirectFlowLogistic"))
                        && (!operation.checkServiceName("MarketplaceServiceItemDropoffSC")
                        && !operation.checkServiceName("MarketplaceServiceItemDropoffPVZ")))
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceServiceItemReturnFlowLogistic")
                                + val.getPriceByServiceNameNoNull("MarketplaceServiceItemDirectFlowLogistic")
                        )));

        return toOfferPrice(collect, skuOffer);
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
     * @param offerSku      Map [offer_id, (sku этого offer_id)]
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

    /**
     * Нахождение компенсаций
     *
     * @param report отчет, полученный от API
     * @return сумма компенсаций за месяц в формате Double
     */
    static public Double getAccrualInternalClaim(@NotNull OZON_FinanceReport report) {
        return report.getResult().getDetails()
                .stream()
                .flatMap(detail -> detail.getOthers().getItems().stream())
                .filter(item -> Objects.equals(item.getName(), "AccrualInternalClaim"))
                .mapToDouble(OZON_FinanceReport.Items::getPrice)
                .sum();
    }
}
