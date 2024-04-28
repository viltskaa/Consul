package com.example.consul.services;

import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.dto.OZON.*;
import com.example.consul.mapping.OZON_dataProcessing;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
                                                  @NotNull String date,
                                                  Function<Map<String, List<OZON_DetailReport.Row>>, Map<String, Integer>> dataFunction) {
        ozonService.setHeader(apiKey, clientId);
        Map<String, List<OZON_DetailReport.Row>> operations = OZON_dataProcessing.groupByOfferId(ozonService.getDetailReport(date).getResult().getRows());
        return dataFunction.apply(operations);
    }

    private Map<String, Double> getDataForMapDouble(@NotNull String apiKey,
                                                    @NotNull String clientId,
                                                    @NotNull String date,
                                                    Function<Map<String, List<OZON_DetailReport.Row>>, Map<String, Double>> dataFunction) {
        ozonService.setHeader(apiKey, clientId);
        Map<String, List<OZON_DetailReport.Row>> operations = OZON_dataProcessing.groupByOfferId(ozonService.getDetailReport(date).getResult().getRows());
        return dataFunction.apply(operations);
    }

    private Map<String, Double> getDataForMapTransaction(@NotNull String apiKey,
                                                        @NotNull String clientId,
                                                        @NotNull String date,
                                                        @NotNull String from,
                                                        @NotNull String to,
                                                        BiFunction<Map<String, List<Long>>,List<OZON_TransactionReport.Operation>, Map<String, Double>> dataFunction) {
        ozonService.setHeader(apiKey, clientId);

        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationAgentStornoDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");
        opT.add("MarketplaceRedistributionOfAcquiringOperation");

        Map<String, List<Long>> offerSku = ozonService.getProductInfoByOfferId(
                        ozonService.getListOfferIdByDate(date))
                .getSkuListByOfferId();

        OZON_TransactionReport request = ozonService.getTransactionReport(
                from, to,
                opT, "all",
                1, 1000);

        List<OZON_TransactionReport.Operation> operations = request.getResult().getOperations();

        for (int i = 2; i <= request.getResult().getPage_count(); i++) {
            operations.addAll(ozonService.getTransactionReport(
                            from, to,
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
            return "Ошибка: " + e.getMessage();
        }
    }

    public Map<String, Integer> getMapSaleCount(@NotNull String apiKey,
                                                @NotNull String clientId,
                                                @NotNull String date) {
        return getDataForMapInt(apiKey, clientId, date, OZON_dataProcessing::saleCount);
    }

    public Map<String, Integer> getMapReturnCount(@NotNull String apiKey,
                                                  @NotNull String clientId,
                                                  @NotNull String date) {
        return getDataForMapInt(apiKey, clientId, date, OZON_dataProcessing::returnCount);
    }

    public Map<String, Double> getMapSaleForDelivered(@NotNull String apiKey,
                                                      @NotNull String clientId,
                                                      @NotNull String date) {
        return getDataForMapDouble(apiKey, clientId, date, OZON_dataProcessing::sumSaleForDelivered);
    }

    public Map<String, Double> getMapSumReturn(@NotNull String apiKey,
                                               @NotNull String clientId,
                                               @NotNull String date) {
        return getDataForMapDouble(apiKey, clientId, date, OZON_dataProcessing::sumReturn);
    }

    public Map<String, Double> getMapSalesCommission(@NotNull String apiKey,
                                                     @NotNull String clientId,
                                                     @NotNull String date) {
        return getDataForMapDouble(apiKey, clientId, date, OZON_dataProcessing::sumSalesCommission);
    }

    public Map<String, Double> getMapLastMile(@NotNull String apiKey,
                                              @NotNull String clientId,
                                              @NotNull String date,
                                              @NotNull String from,
                                              @NotNull String to) {
        return getDataForMapTransaction(apiKey, clientId, date,from,to,OZON_dataProcessing::sumLastMile);
    }

    public Map<String, Double> getMapAcquiring(@NotNull String apiKey,
                                               @NotNull String clientId,
                                               @NotNull String date,
                                               @NotNull String from,
                                               @NotNull String to) {
        return getDataForMapTransaction(apiKey, clientId, date,from,to,OZON_dataProcessing::sumAcquiring);
    }

    public Map<String, Double> getMapReturnDelivery(@NotNull String apiKey,
                                                    @NotNull String clientId,
                                                    @NotNull String date,
                                                    @NotNull String from,
                                                    @NotNull String to) {
        return getDataForMapTransaction(apiKey, clientId, date,from,to,OZON_dataProcessing::sumReturnDelivery);
    }

    public Map<String, Double> getMapReturnProcessing(@NotNull String apiKey,
                                                      @NotNull String clientId,
                                                      @NotNull String date,
                                                      @NotNull String from,
                                                      @NotNull String to) {
        return getDataForMapTransaction(apiKey, clientId, date,from,to,OZON_dataProcessing::sumReturnProcessing);
    }

    public Map<String, Double> getMapShipmentProcessing(@NotNull String apiKey,
                                                        @NotNull String clientId,
                                                        @NotNull String date,
                                                        @NotNull String from,
                                                        @NotNull String to) {
        return getDataForMapTransaction(apiKey, clientId, date,from,to,OZON_dataProcessing::sumShipmentProcessing);
    }

    public Map<String, Double> getMapLogistic(@NotNull String apiKey,
                                              @NotNull String clientId,
                                              @NotNull String date,
                                              @NotNull String from,
                                              @NotNull String to) {
        return getDataForMapTransaction(apiKey, clientId, date,from,to,OZON_dataProcessing::sumLogistic);
    }

    public Map<String, Double> getMapStencils(@NotNull String clientId,
                                              @NotNull String clientSecret,
                                              @NotNull String date) {
        String dateFrom = date + "-01";
        String dateTo = date + "-31";

        ozonService.getPerformanceToken(clientId, clientSecret);

        OZON_PerformanceCampaigns ozonPerformanceCampaigns = ozonService.getCampaigns(
                clientId,
                dateFrom,
                dateTo
        );

        List<String> activeCampaignList = ozonPerformanceCampaigns.getRows()
                .stream().filter(x -> x.getStatus().equals("running"))
                .map(OZON_PerformanceCampaigns.OZON_PerformanceCampaign::getId).toList();

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
                ozonService.getListOfferIdByDate(date))
                .getSkuListByOfferId();

        return OZON_dataProcessing
                .sumStencilByOfferId(priceStencilsBySku, offerSku);
    }

    public List<OZON_TableRow> mergeMapsToTableRows(@NotNull String apiKey,
                                               @NotNull String clientId,
                                               @NotNull String date,
                                               @NotNull String from,
                                               @NotNull String to) {

        Map<String, Integer> saleCount = getMapSaleCount(apiKey, clientId, date);
        Map<String, Integer> returnCount = getMapReturnCount(apiKey, clientId, date);
        Map<String, Double> saleForDelivered = getMapSaleForDelivered(apiKey, clientId, date);
        Map<String, Double> sumReturn = getMapSumReturn(apiKey, clientId, date);
        Map<String, Double> salesCommission = getMapSalesCommission(apiKey, clientId, date);
        Map<String, Double> shipmentProcessing = getMapShipmentProcessing(apiKey, clientId, date, from, to);
        Map<String, Double> logistic = getMapLogistic(apiKey, clientId, date, from, to);
        Map<String, Double> lastMile = getMapLastMile(apiKey, clientId, date, from, to);
        Map<String, Double> acquiring = getMapAcquiring(apiKey, clientId, date, from, to);
        Map<String, Double> returnProcessing = getMapReturnProcessing(apiKey, clientId, date, from, to);
        Map<String, Double> returnDelivery = getMapReturnDelivery(apiKey, clientId, date, from, to);

        Map<String, List<Object>> mergedMap = new HashMap<>(saleCount.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.asList(entry.getValue(),
                        returnCount.get(entry.getKey()), saleForDelivered.get(entry.getKey()),
                        sumReturn.get(entry.getKey()), salesCommission.get(entry.getKey()),
                        shipmentProcessing.get(entry.getKey()), logistic.get(entry.getKey()),
                        lastMile.get(entry.getKey()), acquiring.get(entry.getKey()),
                        returnProcessing.get(entry.getKey()), returnDelivery.get(entry.getKey())
                ))));

        List<OZON_TableRow> listRow=new ArrayList<>();

        for(Map.Entry<String,List<Object>> item : mergedMap.entrySet()){
            OZON_TableRow tableRow = new OZON_TableRow(item.getKey(),
                    (Integer) item.getValue().get(0),
                    (Integer) item.getValue().get(1),
                    (Double) item.getValue().get(2),
                    (Double) item.getValue().get(3),
                    (Double) item.getValue().get(4),
                    (Double) item.getValue().get(5)*(-1),
                    (Double) item.getValue().get(6)*(-1),
                    (Double) item.getValue().get(7)*(-1),
                    (Double) item.getValue().get(8)*(-1),
                    0.0,
                    (Double) item.getValue().get(9)*(-1),
                    (Double) item.getValue().get(10)*(-1),
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0);
            listRow.add(tableRow);
        }

        return listRow;
    }
}
