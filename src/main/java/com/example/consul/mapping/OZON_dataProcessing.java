package com.example.consul.mapping;

import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.dto.OZON.OZON_FinanceReport;
import com.example.consul.dto.OZON.OZON_PerformanceReport;
import com.example.consul.dto.OZON.OZON_TransactionReport;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class OZON_dataProcessing {

    static public List<Long> getSkusFromTransactions(List<OZON_TransactionReport.Operation> operations) {
        return operations.stream().
                flatMap(operation -> operation.getItems().stream())
                .map(OZON_TransactionReport.Item::getSku)
                .collect(Collectors.toList());
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
        Map<String, Integer> result = groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(row -> row.getDeliveryCommission() != null)
                                .mapToInt(row -> row.getDeliveryCommission().getQuantity())
                                .sum()));
        result.put("none", 0);
        return result;
    }

    static public Integer totalDeliveryCount(List<OZON_DetailReport.Row> data) {
        return data.stream()
                .filter(row -> row.getDeliveryCommission() != null)
                .mapToInt(row -> row.getDeliveryCommission().getQuantity())
                .sum();
    }

    static public Integer totalReturnCount(List<OZON_DetailReport.Row> data) {
        return data.stream()
                .filter(row -> row.getReturnCommission() != null)
                .mapToInt(row -> row.getReturnCommission().getQuantity())
                .sum();
    }

    /**
     * Нахождение количества возвращенных товаров по артикулу
     *
     * @param groupMap Map [offer_id, (rows с этим offer_id)]
     * @return Map [offer_id, (возвращено для этого товара)]
     */
    static public Map<String, Integer> returnCount(Map<String, List<OZON_DetailReport.Row>> groupMap) {
        Map<String, Integer> result = groupMap
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(row -> row.getReturnCommission() != null)
                                .mapToInt(row -> row.getReturnCommission().getQuantity())
                                .sum()));
        result.put("none", 0);
        return result;
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
     * Нахождение эквайринга по артикулу
     *
     * @param offerSku   Map [offer_id, (sku этого offer_id)]
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

    /**
     * Нахождение Ozon Рассрочки по артикулу
     *
     * @param offerSku   Map [offer_id, (sku этого offer_id)]
     * @param operations список операций ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (Ozon Рассрочка для товара)]
     */
    static public Map<String, Double> sumInstallments(Map<String, List<Long>> offerSku, List<OZON_TransactionReport.Operation> operations) {
        Map<String, Double> result = offerSku.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                        .mapToDouble(row -> operations.stream()
                                .filter(op -> op.hasSkus(entry.getValue())
                                        && op.getPriceByServiceName("MarketplaceServiceItemInstallment") != null)
                                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSku,
                                        Collectors.summingDouble(OZON_TransactionReport.Operation::getPrice)))
                                .entrySet().stream().filter(o -> o.getKey().equals(row))
                                .mapToDouble(Map.Entry::getValue).sum())
                        .sum()
        ));
        result.put("none", operations.stream()
                .filter(op -> op.getItems().isEmpty())
//                .filter(op -> op.getPriceByServiceName("MarketplaceRedistributionOfAcquiringOperation") != null)
                .mapToDouble(item -> item.getPriceByServiceNameNoNull("MarketplaceServiceItemInstallment")).sum());
        return result;
    }

    /**
     * Нахождение Ozon Премиум
     *
     * @param operations список операций ( OZON_TransactionReport => result => operations )
     * @return Общая сумма Ozon Premium
     */
    static public Double perOzonPremium(List<OZON_DetailReport.Row> rows, List<OZON_TransactionReport.Operation> operations) {
        Double totalSumOzonPremium = operations.stream()
                .filter(op -> Objects.equals(op.getOperation_type(), "OperationMarketplacePremiumSubscribtion"))
                .mapToDouble(OZON_TransactionReport.Operation::getAmount)
                .sum();
        Integer div = totalDeliveryCount(rows) - totalReturnCount(rows);
        return totalSumOzonPremium / div;
    }

    static public Double perBuyReview(List<OZON_DetailReport.Row> rows, List<OZON_TransactionReport.Operation> operations) {
        Double totalSumBuyReview = operations.stream()
                .filter(op -> Objects.equals(op.getOperation_type(), "MarketplaceSaleReviewsOperation"))
                .mapToDouble(OZON_TransactionReport.Operation::getAmount)
                .sum();
        Integer div = totalDeliveryCount(rows) - totalReturnCount(rows);
        return totalSumBuyReview / div;
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
     * Нахождение последней мили по артикулу
     *
     * @param offerSku   Map [offer_id, (sku этого offer_id)]
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
     * @param offerSku   Map [offer_id, (sku этого offer_id)]
     * @param operations список операций ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (сумма для этого артикля)]
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
     * Нахождение услуга продвижения «Бонусы продавца» по артикулу
     *
     * @param offerSku   Map [offer_id, (sku этого offer_id)]
     * @param operations список операций ( OZON_TransactionReport => result => operations )
     * @return Map [offer_id, (сумма для этого артикля)]
     */
    static public Map<String, Double> sumCashbackIndividualPoints(Map<String, List<Long>> offerSku, List<OZON_TransactionReport.Operation> operations) {
        return offerSku.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> operations.stream()
                                .filter(op -> op.hasSkus(entry.getValue()))
                                .filter(s -> s.getServices() != null && s.getServices().stream()
                                        .anyMatch(x -> "MarketplaceServicePremiumCashbackIndividualPoints".equals(x.getName())))
                                .mapToDouble(x -> x.getServices().stream()
                                        .filter(y -> "MarketplaceServicePremiumCashbackIndividualPoints".equals(y.getName()))
                                        .mapToDouble(OZON_TransactionReport.Service::getPrice)
                                        .sum())
                                .sum()
                ));
    }


    /**
     * Нахождение обработки отправления по артикулу
     *
     * @param offerSku   Map [offer_id, (sku этого offer_id)]
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
     * @param offerSku   Map [offer_id, (sku этого offer_id)]
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
                                        .anyMatch(x -> "MarketplaceServiceItemRedistributionReturnsPVZ".equals(x.getName())
                                                || "MarketplaceServiceItemReturnNotDelivToCustomer".equals(x.getName())
                                                || "MarketplaceServiceItemReturnPartGoodsCustomer".equals(x.getName())))
                                .mapToDouble(x -> x.getServices().stream()
                                        .filter(y -> "MarketplaceServiceItemRedistributionReturnsPVZ".equals(y.getName())
                                                || "MarketplaceServiceItemReturnNotDelivToCustomer".equals(y.getName())
                                                || "MarketplaceServiceItemReturnPartGoodsCustomer".equals(y.getName()))
                                        .mapToDouble(OZON_TransactionReport.Service::getPrice)
                                        .sum())
                                .sum()
                ));
    }

    /**
     * Нахождение доставки возврата по артикулу
     *
     * @param offerSku   Map [offer_id, (sku этого offer_id)]
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
                                        .anyMatch(x -> "MarketplaceServiceItemReturnFlowLogistic".equals(x.getName())))
                                .mapToDouble(x -> x.getServices().stream()
                                        .filter(y -> "MarketplaceServiceItemReturnFlowLogistic".equals(y.getName()))
                                        .mapToDouble(OZON_TransactionReport.Service::getPrice)
                                        .sum())
                                .sum()
                ));
    }

    /**
     * Нахождение кросс-докинга
     *
     * @param operations
     * @return
     */
    static public Double perCrossDocking(List<OZON_DetailReport.Row> rows, List<OZON_TransactionReport.Operation> operations) {
        Double totalSumCrossDocking = operations.stream()
                .filter(op -> Objects.equals(op.getOperation_type(), "OperationMarketplaceCrossDockServiceWriteOff"))
                .mapToDouble(OZON_TransactionReport.Operation::getAmount)
                .sum();
        Integer div = totalDeliveryCount(rows) - totalReturnCount(rows);
        return totalSumCrossDocking / div;
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
                .filter(item -> Objects.equals(item.getName(), "AccrualInternalClaim")
                )
                .mapToDouble(OZON_FinanceReport.Items::getPrice)
                .sum();
    }

    static public Double getCompensation(@NotNull List<OZON_DetailReport.Row> rows, @NotNull List<OZON_TransactionReport.Operation> operation) {
        Double totalCompensation = operation.stream()
                .filter(op -> Objects.equals(op.getType(), "compensation"))
                .mapToDouble(OZON_TransactionReport.Operation::getAmount)
                .sum();
        Integer div = totalDeliveryCount(rows) - totalReturnCount(rows);
        return totalCompensation / div;
    }

    static public Double getDisposal(@NotNull List<OZON_DetailReport.Row> rows, @NotNull OZON_FinanceReport report) {
        Double totalDisposal = report.getResult().getDetails()
                .stream()
                .flatMap(detail -> detail.getServices().getItems().stream())
                .filter(item -> Objects.equals(item.getName(), "MarketplaceServiceStockDisposal"))
                .mapToDouble(OZON_FinanceReport.Items::getPrice)
                .sum();
        Integer div = totalDeliveryCount(rows) - totalReturnCount(rows);
        return totalDisposal / div;
    }
}
