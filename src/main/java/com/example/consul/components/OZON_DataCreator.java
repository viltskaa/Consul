package com.example.consul.components;

import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.dto.OZON.*;
import com.example.consul.mapping.OZON_dataProcessing;
import org.antlr.v4.runtime.misc.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class OZON_DataCreator {
    private Map<String, Integer> getDataForMapInt(@NotNull OZON_DetailReport ozonDetailReport,
                                                  @NotNull Function<Map<String, List<OZON_DetailReport.Row>>, Map<String, Integer>> dataFunction) {
        return dataFunction.apply(
                OZON_dataProcessing.groupByOfferId(ozonDetailReport.getResult().getRows())
        );
    }

    private Map<String, Double> getDataForMapDouble(@NotNull OZON_DetailReport ozonDetailReport,
                                                    @NotNull Function<Map<String, List<OZON_DetailReport.Row>>, Map<String, Double>> dataFunction) {
        return dataFunction.apply(
                OZON_dataProcessing.groupByOfferId(ozonDetailReport.getResult().getRows())
        );
    }

    private Map<String, Double> getDataForMapTransaction(@NotNull Map<String, List<Long>> offerSkus,
                                                         @NotNull OZON_TransactionReport ozonTransactionReport,
                                                         @NotNull BiFunction<Map<String, List<Long>>, List<OZON_TransactionReport.Operation>, Map<String, Double>> dataFunction) {
        return dataFunction.apply(
                offerSkus,
                ozonTransactionReport.getResult().getOperations()
        );
    }

    public Pair<String, String> getStartAndEndDateToUtc(Integer month, Integer year) {
        LocalDate date = LocalDate.of(year, month, 1);

        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        String startOfMonthString = startOfMonth.atStartOfDay(ZoneOffset.UTC)
                .toString().replace("T00:00", "T00:00:00.000");
        String endOfMonthString = endOfMonth.atStartOfDay(ZoneOffset.UTC)
                .plusDays(1).minusNanos(1000000).toString();

        return new Pair<>(startOfMonthString, endOfMonthString);
    }

    public Pair<String, String> getStartAndEndDateToDate(Integer month, Integer year) {
        LocalDate date = LocalDate.of(year, month, 1);
        return new Pair<>(date.withDayOfMonth(1).toString(), date.withDayOfMonth(date.lengthOfMonth()).toString());
    }

    public Map<String, Integer> getMapSaleCount(@NotNull OZON_DetailReport ozonDetailReport) {
        return getDataForMapInt(ozonDetailReport, OZON_dataProcessing::saleCount);
    }

    public Map<String, Integer> getMapReturnCount(@NotNull OZON_DetailReport ozonDetailReport) {
        return getDataForMapInt(ozonDetailReport, OZON_dataProcessing::returnCount);
    }

    public Map<String, Double> getMapSaleForDelivered(@NotNull OZON_DetailReport ozonDetailReport) {
        return getDataForMapDouble(ozonDetailReport, OZON_dataProcessing::sumSaleForDelivered);
    }

    public Map<String, Double> getMapSumReturn(@NotNull OZON_DetailReport ozonDetailReport) {
        return getDataForMapDouble(ozonDetailReport, OZON_dataProcessing::sumReturn);
    }

    public Map<String, Double> getMapSalesCommission(@NotNull OZON_DetailReport ozonDetailReport) {
        return getDataForMapDouble(ozonDetailReport, OZON_dataProcessing::sumSalesCommission);
    }

    public Map<String, Double> getMapLastMile(@NotNull Map<String, List<Long>> offerSkus,
                                              @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(offerSkus, ozonTransactionReport, OZON_dataProcessing::sumLastMile);
    }

    public Map<String, Double> getMapAcquiring(@NotNull Map<String, List<Long>> offerSkus,
                                               @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(offerSkus, ozonTransactionReport, OZON_dataProcessing::sumAcquiring);
    }

    public Map<String, Double> getInstallments(@NotNull Map<String, List<Long>> offerSkus,
                                               @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(offerSkus, ozonTransactionReport, OZON_dataProcessing::sumInstallments);
    }

    public Map<String, Double> getMapReturnDelivery(@NotNull Map<String, List<Long>> offerSkus,
                                                    @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(offerSkus, ozonTransactionReport, OZON_dataProcessing::sumReturnDelivery);
    }

    public Map<String, Double> getMapReturnProcessing(@NotNull Map<String, List<Long>> offerSkus,
                                                      @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(offerSkus, ozonTransactionReport, OZON_dataProcessing::sumReturnProcessing);
    }

    public Map<String, Double> getMapShipmentProcessing(@NotNull Map<String, List<Long>> offerSkus,
                                                        @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(offerSkus, ozonTransactionReport, OZON_dataProcessing::sumShipmentProcessing);
    }

    public Map<String, Double> getMapLogistic(@NotNull Map<String, List<Long>> offerSkus,
                                              @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(offerSkus, ozonTransactionReport, OZON_dataProcessing::sumLogistic);
    }

    public Map<String, Double> getMapCashbackIndividualPoints(@NotNull Map<String, List<Long>> offerSkus,
                                                              @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(offerSkus, ozonTransactionReport, OZON_dataProcessing::sumCashbackIndividualPoints);
    }

    public Map<String, Double> getMapStencils(@NotNull Map<String, List<Long>> offerSkus,
                                              @NotNull List<OZON_PerformanceReport> ozonPerformanceReports) {
        return OZON_dataProcessing.sumStencilByOfferId(
                OZON_dataProcessing.sumStencilBySku(ozonPerformanceReports),
                offerSkus
        );
    }

    public Double getAccrualInternalClaim(@NotNull OZON_FinanceReport ozonFinanceReport) {
        return OZON_dataProcessing.getAccrualInternalClaim(ozonFinanceReport);
    }

    public Double getOzonPremium(@NotNull OZON_DetailReport ozonDetailReport,
                                 @NotNull OZON_TransactionReport ozonTransactionReport) {
        return OZON_dataProcessing.perOzonPremium(
                ozonDetailReport.getResult().getRows(),
                ozonTransactionReport.getResult().getOperations()
        );
    }

    public Double getDisposal(@NotNull OZON_DetailReport ozonDetailReport,
                              @NotNull OZON_FinanceReport ozonFinanceReport) {
        return OZON_dataProcessing.getDisposal(
                ozonDetailReport.getResult().getRows(),
                ozonFinanceReport
        );
    }

    public Double getCompensation(@NotNull OZON_DetailReport ozonDetailReport,
                                  @NotNull OZON_TransactionReport ozonTransactionReport) {
        return OZON_dataProcessing.getCompensation(
                ozonDetailReport.getResult().getRows(),
                ozonTransactionReport.getResult().getOperations()
        );
    }

    public Double getBuyReview(@NotNull OZON_DetailReport ozonDetailReport,
                               @NotNull OZON_TransactionReport ozonTransactionReport) {
        return OZON_dataProcessing.perBuyReview(
                ozonDetailReport.getResult().getRows(),
                ozonTransactionReport.getResult().getOperations()
        );
    }

    public Double getActionCost(@NotNull OZON_TransactionReport ozonTransactionReport) {
        return OZON_dataProcessing.sumActionCost(ozonTransactionReport.getResult().getOperations());
    }

    public Double getCrossDocking(@NotNull OZON_DetailReport ozonDetailReport,
                                  @NotNull OZON_TransactionReport ozonTransactionReport) {
        return OZON_dataProcessing.perCrossDocking(
                ozonDetailReport.getResult().getRows(),
                ozonTransactionReport.getResult().getOperations()
        );
    }


    public List<OZON_TableRow> mergeMapsToTableRows(@NotNull OZON_DetailReport ozonDetailReport,
                                                    @NotNull Map<String, List<Long>> offerSkus,
                                                    @NotNull OZON_TransactionReport ozonTransactionReport,
                                                    @NotNull List<OZON_PerformanceReport> ozonPerformanceReports,
                                                    @NotNull OZON_FinanceReport ozonFinanceReport) {
        Map<String, Integer> saleCount = getMapSaleCount(ozonDetailReport);
        Map<String, Integer> returnCount = getMapReturnCount(ozonDetailReport);
        Map<String, Double> saleForDelivered = getMapSaleForDelivered(ozonDetailReport);
        Map<String, Double> sumReturn = getMapSumReturn(ozonDetailReport);
        Map<String, Double> salesCommission = getMapSalesCommission(ozonDetailReport);
        Map<String, Double> shipmentProcessing = getMapShipmentProcessing(offerSkus, ozonTransactionReport);
        Map<String, Double> logistic = getMapLogistic(offerSkus, ozonTransactionReport);
        Map<String, Double> lastMile = getMapLastMile(offerSkus, ozonTransactionReport);
        Map<String, Double> acquiring = getMapAcquiring(offerSkus, ozonTransactionReport);
        Map<String, Double> returnProcessing = getMapReturnProcessing(offerSkus, ozonTransactionReport);
        Map<String, Double> returnDelivery = getMapReturnDelivery(offerSkus, ozonTransactionReport);
        Map<String, Double> stencilProduct = getMapStencils(offerSkus, ozonPerformanceReports);
        Map<String, Double> cashbackIndividualPoints = getMapCashbackIndividualPoints(offerSkus, ozonTransactionReport);
        Double compensation = getCompensation(ozonDetailReport, ozonTransactionReport);
        Double ozonPremium = getOzonPremium(ozonDetailReport, ozonTransactionReport);
        Double disposal = getDisposal(ozonDetailReport, ozonFinanceReport);
        Double buyReview = getBuyReview(ozonDetailReport, ozonTransactionReport);
        //Double actionCost = getActionCost(ozonTransactionReport);
        Map<String, Double> installments = getInstallments(offerSkus, ozonTransactionReport);
        Double crossDocking = getCrossDocking(ozonDetailReport,ozonTransactionReport);

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(saleCount.keySet());
        allKeys.addAll(returnCount.keySet());
        allKeys.addAll(saleForDelivered.keySet());
        allKeys.addAll(sumReturn.keySet());
        allKeys.addAll(salesCommission.keySet());
        allKeys.addAll(shipmentProcessing.keySet());
        allKeys.addAll(logistic.keySet());
        allKeys.addAll(lastMile.keySet());
        allKeys.addAll(acquiring.keySet());
        allKeys.addAll(returnProcessing.keySet());
        allKeys.addAll(returnDelivery.keySet());
        allKeys.addAll(stencilProduct.keySet());
        allKeys.addAll(cashbackIndividualPoints.keySet());
        allKeys.addAll(installments.keySet());

        Map<String, List<Object>> mergedMap = new HashMap<>();
        for (String key : allKeys) {
            mergedMap.put(key, Arrays.asList(
                    saleCount.getOrDefault(key, 0), // 0
                    returnCount.getOrDefault(key, 0), // 1
                    saleForDelivered.getOrDefault(key, 0.0), // 2
                    sumReturn.getOrDefault(key, 0.0), // 3
                    salesCommission.getOrDefault(key, 0.0), // 4
                    shipmentProcessing.getOrDefault(key, 0.0), // 5
                    logistic.getOrDefault(key, 0.0), // 6
                    lastMile.getOrDefault(key, 0.0), // 7
                    acquiring.getOrDefault(key, 0.0), // 8
                    returnProcessing.getOrDefault(key, 0.0), // 9
                    returnDelivery.getOrDefault(key, 0.0), // 10
                    cashbackIndividualPoints.getOrDefault(key, 0.0), // 11
                    stencilProduct.getOrDefault(key, 0.0), // 12
                    installments.getOrDefault(key, 0.0) // 13
            ));
        }

        return mergedMap.entrySet().stream().map(entry -> {
            String article = entry.getKey();
            List<Object> values = entry.getValue();
            Integer deliveredCount = (Integer) values.get(0);
            Integer returnedCount = (Integer) values.get(1);
            return OZON_TableRow.builder()
                    .article(article)
                    .delivered(deliveredCount)
                    .returned(returnedCount)
                    .saleForDelivered((Double) values.get(2))
                    .sumReturn((Double) values.get(3))
                    .salesCommission((Double) values.get(4))
                    .shipmentProcessing((Double) values.get(5) * -1)
                    .logistic((Double) values.get(6) * -1)
                    .lastMile((Double) values.get(7) * -1)
                    .acquiring((Double) values.get(8) * -1)
                    .installment((Double) values.get(13) * -1)
                    .returnProcessing((Double) values.get(9) * -1)
                    .returnDelivery((Double) values.get(10) * -1)
                    //.promotion(actionCost / mergedMap.size() * -1)
                    .compensation(compensation  * (deliveredCount - returnedCount))
                    .searchPromotion(0.0)
                    .cashbackIndividualPoints((Double) values.get(11) * -1)
                    .stencilProduct((Double) values.get(12))
                    .ozonPremium(ozonPremium * (deliveredCount - returnedCount) * -1)
                    .crossDockingDelivery(crossDocking * (deliveredCount - returnedCount) * -1)
                    //.claimsAccruals(0.0)
                    .buyReview(buyReview * (deliveredCount - returnedCount) * -1)
                    .disposal(disposal * (deliveredCount - returnedCount) * -1)
                    .build();
        }).toList();
    }
}
