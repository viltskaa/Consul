package com.example.consul.services;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class OZON_ExcelCreator {
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

    private Map<String, Double> getDataForMapTransaction(@NotNull OZON_SkuProductsReport ozonSkuProductsReport,
                                                         @NotNull OZON_TransactionReport ozonTransactionReport,
                                                         @NotNull BiFunction<Map<String, List<Long>>, List<OZON_TransactionReport.Operation>, Map<String, Double>> dataFunction) {
        return dataFunction.apply(
                ozonSkuProductsReport.getSkuListByOfferId(),
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

    public Map<String, Double> getMapLastMile(@NotNull OZON_SkuProductsReport ozonSkuProductsReport,
                                              @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(ozonSkuProductsReport, ozonTransactionReport, OZON_dataProcessing::sumLastMile);
    }

    public Map<String, Double> getMapAcquiring(@NotNull OZON_SkuProductsReport ozonSkuProductsReport,
                                               @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(ozonSkuProductsReport, ozonTransactionReport, OZON_dataProcessing::sumAcquiring);
    }

    public Map<String, Double> getMapReturnDelivery(@NotNull OZON_SkuProductsReport ozonSkuProductsReport,
                                                    @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(ozonSkuProductsReport, ozonTransactionReport, OZON_dataProcessing::sumReturnDelivery);
    }

    public Map<String, Double> getMapReturnProcessing(@NotNull OZON_SkuProductsReport ozonSkuProductsReport,
                                                      @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(ozonSkuProductsReport, ozonTransactionReport, OZON_dataProcessing::sumReturnProcessing);
    }

    public Map<String, Double> getMapShipmentProcessing(@NotNull OZON_SkuProductsReport ozonSkuProductsReport,
                                                        @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(ozonSkuProductsReport, ozonTransactionReport, OZON_dataProcessing::sumShipmentProcessing);
    }

    public Map<String, Double> getMapLogistic(@NotNull OZON_SkuProductsReport ozonSkuProductsReport,
                                              @NotNull OZON_TransactionReport ozonTransactionReport) {
        return getDataForMapTransaction(ozonSkuProductsReport, ozonTransactionReport, OZON_dataProcessing::sumLogistic);
    }

    public Map<String, Double> getMapStencils(@NotNull OZON_SkuProductsReport ozonSkuProductsReport,
                                              @NotNull List<OZON_PerformanceReport> ozonPerformanceReports) {
        return OZON_dataProcessing.sumStencilByOfferId(
                OZON_dataProcessing.sumStencilBySku(ozonPerformanceReports),
                ozonSkuProductsReport.getSkuListByOfferId()
        );
    }

    public List<OZON_TableRow> mergeMapsToTableRows(@NotNull OZON_DetailReport ozonDetailReport,
                                                    @NotNull OZON_SkuProductsReport ozonSkuProductsReport,
                                                    @NotNull OZON_TransactionReport ozonTransactionReport,
                                                    @NotNull List<OZON_PerformanceReport> ozonPerformanceReports) {

        Map<String, Integer> saleCount = getMapSaleCount(ozonDetailReport);
        Map<String, Integer> returnCount = getMapReturnCount(ozonDetailReport);
        Map<String, Double> saleForDelivered = getMapSaleForDelivered(ozonDetailReport);
        Map<String, Double> sumReturn = getMapSumReturn(ozonDetailReport);
        Map<String, Double> salesCommission = getMapSalesCommission(ozonDetailReport);
        Map<String, Double> shipmentProcessing = getMapShipmentProcessing(ozonSkuProductsReport, ozonTransactionReport);
        Map<String, Double> logistic = getMapLogistic(ozonSkuProductsReport, ozonTransactionReport);
        Map<String, Double> lastMile = getMapLastMile(ozonSkuProductsReport, ozonTransactionReport);
        Map<String, Double> acquiring = getMapAcquiring(ozonSkuProductsReport, ozonTransactionReport);
        Map<String, Double> returnProcessing = getMapReturnProcessing(ozonSkuProductsReport, ozonTransactionReport);
        Map<String, Double> returnDelivery = getMapReturnDelivery(ozonSkuProductsReport, ozonTransactionReport);
        Map<String, Double> stencilProduct = getMapStencils(ozonSkuProductsReport, ozonPerformanceReports);

        Map<String, List<Object>> mergedMap = new HashMap<>(saleCount.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.asList(entry.getValue(),
                        returnCount.getOrDefault(entry.getKey(), 0),
                        saleForDelivered.getOrDefault(entry.getKey(), 0.0),
                        sumReturn.getOrDefault(entry.getKey(), 0.0),
                        salesCommission.getOrDefault(entry.getKey(), 0.0),
                        shipmentProcessing.getOrDefault(entry.getKey(), 0.0),
                        logistic.getOrDefault(entry.getKey(), 0.0),
                        lastMile.getOrDefault(entry.getKey(), 0.0),
                        acquiring.getOrDefault(entry.getKey(), 0.0),
                        returnProcessing.getOrDefault(entry.getKey(), 0.0),
                        returnDelivery.getOrDefault(entry.getKey(), 0.0),
                        stencilProduct.getOrDefault(entry.getKey(), 0.0)
                ))));

        return mergedMap.entrySet().stream().map(x -> {
            List<Object> values = x.getValue();
            return OZON_TableRow.builder()
                    .offerId(x.getKey())
                    .delivered((Integer) values.get(0))
                    .returned((Integer) values.get(1))
                    .saleForDelivered((Double) values.get(2))
                    .sumReturn((Double) values.get(3))
                    .salesCommission((Double) values.get(4))
                    .shipmentProcessing((Double) values.get(5) * -1)
                    .logistic((Double) values.get(6) * -1)
                    .lastMile((Double) values.get(7) * -1)
                    .acquiring((Double) values.get(8) * -1)
                    .installment(0.0)
                    .returnProcessing((Double) values.get(9) * -1)
                    .returnDelivery((Double) values.get(10) * -1)
                    .promotion(0.0)
                    .compensation(0.0)
                    .searchPromotion(0.0)
                    .stencilProduct((Double) values.get(11))
                    .ozonPremium(0.0)
                    .crossDockingDelivery(0.0)
                    .claimsAccruals(0.0)
                    .build();
        }).toList();
    }
}
