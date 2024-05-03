package com.example.consul.services;

import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.dto.OZON.*;
import com.example.consul.mapping.OZON_dataProcessing;
import org.antlr.v4.runtime.misc.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExcelService {
    private final OZON_Service ozonService;

    public ExcelService(OZON_Service ozonService) {
        this.ozonService = ozonService;
    }

    private Map<String, Integer> getDataForMapInt(@NotNull String apiKey,
                                                  @NotNull String clientId,
                                                  @NotNull Integer month,
                                                  @NotNull Integer year,
                                                  Function<Map<String, List<OZON_DetailReport.Row>>, Map<String, Integer>> dataFunction) {
        ozonService.setHeader(apiKey, clientId);
        Map<String, List<OZON_DetailReport.Row>> operations = OZON_dataProcessing
                                                                .groupByOfferId(ozonService
                                                                        .getDetailReport(month, year)
                                                                        .getResult().getRows());
        return dataFunction.apply(operations);
    }

    private Map<String, Double> getDataForMapDouble(@NotNull String apiKey,
                                                    @NotNull String clientId,
                                                    @NotNull Integer month,
                                                    @NotNull Integer year,
                                                    Function<Map<String, List<OZON_DetailReport.Row>>, Map<String, Double>> dataFunction) {
        ozonService.setHeader(apiKey, clientId);
        Map<String, List<OZON_DetailReport.Row>> operations = OZON_dataProcessing
                                                                .groupByOfferId(ozonService
                                                                        .getDetailReport(month, year)
                                                                        .getResult().getRows());
        return dataFunction.apply(operations);
    }

    private Map<String, Double> getDataForMapTransaction(@NotNull String apiKey,
                                                         @NotNull String clientId,
                                                         @NotNull Integer month,
                                                         @NotNull Integer year,
                                                         BiFunction<Map<String, List<Long>>, List<OZON_TransactionReport.Operation>, Map<String, Double>> dataFunction) {
        ozonService.setHeader(apiKey, clientId);

        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationAgentStornoDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");
        opT.add("MarketplaceRedistributionOfAcquiringOperation");

        Map<String, List<Long>> offerSku = ozonService.getProductInfoByOfferId(
                        ozonService.getListOfferIdByDate(month, year))
                .getSkuListByOfferId();

        Pair<String, String> pairDate =getStartAndEndDateToUtc(month, year);

        OZON_TransactionReport request = ozonService.getTransactionReport(
                pairDate.a, pairDate.b,
                opT, "all",
                1, 1000);

        List<OZON_TransactionReport.Operation> operations = request.getResult().getOperations();

        for (int i = 2; i <= request.getResult().getPage_count(); i++) {
            operations.addAll(ozonService.getTransactionReport(
                            pairDate.a, pairDate.b,
                            opT, "all", i, 1000)
                    .getResult().getOperations());
        }

        return dataFunction.apply(offerSku, operations);
    }

    public String getMonthNameAndYear(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

        try {
            Date date = dateFormat.parse(dateStr);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLLL yyyy");

            return simpleDateFormat.format(date).toUpperCase();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public Pair<String,String> getStartAndEndDateToUtc(Integer month, Integer year) {
        LocalDate date = LocalDate.of(year, month, 1);

        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        String startOfMonthString = startOfMonth.atStartOfDay(ZoneOffset.UTC)
                .toString().replace("T00:00", "T00:00:00.000");
        String endOfMonthString = endOfMonth.atStartOfDay(ZoneOffset.UTC)
                .plusDays(1).minusNanos(1000000).toString();

        return new Pair<>(startOfMonthString,endOfMonthString);
    }

    public Pair<String, String> getStartAndEndDateToDate(Integer month, Integer year) {
        LocalDate date = LocalDate.of(year, month, 1);
        return new Pair<>(date.withDayOfMonth(1).toString(), date.withDayOfMonth(date.lengthOfMonth()).toString());
    }

    public Map<String, Integer> getMapSaleCount(@NotNull String apiKey,
                                                @NotNull String clientId,
                                                @NotNull Integer month,
                                                @NotNull Integer year) {
        return getDataForMapInt(apiKey, clientId,
                month, year,
                OZON_dataProcessing::saleCount);
    }

    public Map<String, Integer> getMapReturnCount(@NotNull String apiKey,
                                                  @NotNull String clientId,
                                                  @NotNull Integer month,
                                                  @NotNull Integer year) {
        return getDataForMapInt(apiKey, clientId,
                month, year,
                OZON_dataProcessing::returnCount);
    }

    public Map<String, Double> getMapSaleForDelivered(@NotNull String apiKey,
                                                      @NotNull String clientId,
                                                      @NotNull Integer month,
                                                      @NotNull Integer year) {
        return getDataForMapDouble(apiKey, clientId,
                month, year,
                OZON_dataProcessing::sumSaleForDelivered);
    }

    public Map<String, Double> getMapSumReturn(@NotNull String apiKey,
                                               @NotNull String clientId,
                                               @NotNull Integer month,
                                               @NotNull Integer year) {
        return getDataForMapDouble(apiKey, clientId,
                month, year,
                OZON_dataProcessing::sumReturn);
    }

    public Map<String, Double> getMapSalesCommission(@NotNull String apiKey,
                                                     @NotNull String clientId,
                                                     @NotNull Integer month,
                                                     @NotNull Integer year) {
        return getDataForMapDouble(apiKey, clientId,
                month, year,
                OZON_dataProcessing::sumSalesCommission);
    }

    public Map<String, Double> getMapLastMile(@NotNull String apiKey,
                                              @NotNull String clientId,
                                              @NotNull Integer month,
                                              @NotNull Integer year) {
        return getDataForMapTransaction(apiKey, clientId,
                month, year,
                OZON_dataProcessing::sumLastMile);
    }

    public Map<String, Double> getMapAcquiring(@NotNull String apiKey,
                                               @NotNull String clientId,
                                               @NotNull Integer month,
                                               @NotNull Integer year) {
        return getDataForMapTransaction(apiKey, clientId,
                month, year,
                OZON_dataProcessing::sumAcquiring);
    }

    public Map<String, Double> getMapReturnDelivery(@NotNull String apiKey,
                                                    @NotNull String clientId,
                                                    @NotNull Integer month,
                                                    @NotNull Integer year) {
        return getDataForMapTransaction(apiKey, clientId,
                month, year,
                OZON_dataProcessing::sumReturnDelivery);
    }

    public Map<String, Double> getMapReturnProcessing(@NotNull String apiKey,
                                                      @NotNull String clientId,
                                                      @NotNull Integer month,
                                                      @NotNull Integer year) {
        return getDataForMapTransaction(apiKey, clientId,
                                        month, year,
                                        OZON_dataProcessing::sumReturnProcessing);
    }

    public Map<String, Double> getMapShipmentProcessing(@NotNull String apiKey,
                                                        @NotNull String clientId,
                                                        @NotNull Integer month,
                                                        @NotNull Integer year) {
        return getDataForMapTransaction(apiKey, clientId,
                month, year,
                OZON_dataProcessing::sumShipmentProcessing);
    }

    public Map<String, Double> getMapLogistic(@NotNull String apiKey,
                                              @NotNull String clientId,
                                              @NotNull Integer month,
                                              @NotNull Integer year) {
        return getDataForMapTransaction(apiKey, clientId,
                month, year,
                OZON_dataProcessing::sumLogistic);
    }

    public Map<String, Double> getMapStencils(@NotNull String clientId,
                                              @NotNull String clientSecret,
                                              @NotNull Integer month,
                                              @NotNull Integer year) {
        Pair<String, String> dateEntry = getStartAndEndDateToDate(month, year);
        String dateFrom = dateEntry.a;
        String dateTo = dateEntry.b;

        ozonService.getPerformanceToken(clientId, clientSecret);

        OZON_PerformanceCampaigns ozonPerformanceCampaigns = ozonService.getCampaigns(
                clientId,
                dateFrom,
                dateTo
        );

        List<String> activeCampaignList = ozonPerformanceCampaigns.getRows()
                .stream().filter(x -> !x.getMoneySpent().equals("0"))
                .map(OZON_PerformanceCampaigns.OZON_PerformanceCampaign::getId).toList();

        if (activeCampaignList.isEmpty()) {
            return Collections.emptyMap();
        }

        OZON_PerformanceStatistic statistic = ozonService.getPerformanceStatisticByCampaignId(
                clientId,
                activeCampaignList,
                dateFrom,
                dateTo
        );

        List<OZON_PerformanceReport> report
                = ozonService.asyncGetPerformanceReportByUUID(clientId, statistic.getUUID());
        if (report == null) {
            return new HashMap<>();
        }

        Map<String, Double> priceStencilsBySku = OZON_dataProcessing.sumStencilBySku(report);
        Map<String, List<Long>> offerSku = ozonService.getProductInfoByOfferId(
                        ozonService.getListOfferIdByDate(month, year))
                .getSkuListByOfferId();

        return OZON_dataProcessing
                .sumStencilByOfferId(priceStencilsBySku, offerSku);
    }

    public List<OZON_TableRow> mergeMapsToTableRows(@NotNull String apiKey,
                                                    @NotNull String clientId,
                                                    @NotNull String performanceClientId,
                                                    @NotNull String performanceClientSecret,
                                                    @NotNull Integer month,
                                                    @NotNull Integer year) {

        Map<String, Integer> saleCount = getMapSaleCount(apiKey, clientId, month, year);
        Map<String, Integer> returnCount = getMapReturnCount(apiKey, clientId, month, year);
        Map<String, Double> saleForDelivered = getMapSaleForDelivered(apiKey, clientId, month, year);
        Map<String, Double> sumReturn = getMapSumReturn(apiKey, clientId, month, year);
        Map<String, Double> salesCommission = getMapSalesCommission(apiKey, clientId, month, year);
        Map<String, Double> shipmentProcessing = getMapShipmentProcessing(apiKey, clientId, month, year);
        Map<String, Double> logistic = getMapLogistic(apiKey, clientId, month, year);
        Map<String, Double> lastMile = getMapLastMile(apiKey, clientId, month, year);
        Map<String, Double> acquiring = getMapAcquiring(apiKey, clientId, month, year);
        Map<String, Double> returnProcessing = getMapReturnProcessing(apiKey, clientId, month, year);
        Map<String, Double> returnDelivery = getMapReturnDelivery(apiKey, clientId, month, year);
        Map<String, Double> stencilProduct = getMapStencils(performanceClientId, performanceClientSecret, month, year);

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

        List<OZON_TableRow> listRow = new ArrayList<>();

        for (Map.Entry<String, List<Object>> item : mergedMap.entrySet()) {
            OZON_TableRow tableRow = new OZON_TableRow(item.getKey(),
                    (Integer) item.getValue().get(0),
                    (Integer) item.getValue().get(1),
                    (Double) item.getValue().get(2),
                    (Double) item.getValue().get(3),
                    (Double) item.getValue().get(4),
                    (Double) item.getValue().get(5) * (-1),
                    (Double) item.getValue().get(6) * (-1),
                    (Double) item.getValue().get(7) * (-1),
                    (Double) item.getValue().get(8) * (-1),
                    0.0,
                    (Double) item.getValue().get(9) * (-1),
                    (Double) item.getValue().get(10) * (-1),
                    0.0,
                    0.0,
                    0.0,
                    (Double) item.getValue().get(11),
                    0.0,
                    0.0,
                    0.0,
                    0.0);
            listRow.add(tableRow);
        }

        return listRow;
    }
}
