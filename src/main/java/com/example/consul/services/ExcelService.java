package com.example.consul.services;

import com.example.consul.dto.OZON.OZON_TransactionReport;
import com.example.consul.mapping.OZON_dataProcessing;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {
    private final OZON_Service ozonService;

    public ExcelService(OZON_Service ozonService) {
        this.ozonService = ozonService;
    }

    public String getMonthNameAndYear(String dateStr){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

        try {
            Date date = dateFormat.parse(dateStr);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLLL yyyy");

            return simpleDateFormat.format(date).toUpperCase();
        } catch (Exception e) {
            return"Ошибка: " + e.getMessage();
        }
    }

    public Map<String, Integer> getMapSaleCount(@NotNull String apiKey,
                                                  @NotNull String clientId,
                                                  @NotNull String date){
        ozonService.setHeader(apiKey, clientId);

        return OZON_dataProcessing.saleCount(OZON_dataProcessing.groupByOfferId(ozonService.getDetailReport(date)
                .getResult().getRows()));
    }

    public Map<String, Integer> getMapReturnCount(@NotNull String apiKey,
                                                      @NotNull String clientId,
                                                      @NotNull String date){
        ozonService.setHeader(apiKey, clientId);

        return OZON_dataProcessing.returnCount(OZON_dataProcessing.groupByOfferId(ozonService.getDetailReport(date)
                .getResult().getRows()));
    }

    public Map<String, Double> getMapSaleForDelivered(@NotNull String apiKey,
                                               @NotNull String clientId,
                                               @NotNull String date){
        ozonService.setHeader(apiKey, clientId);

        return OZON_dataProcessing.sumSaleForDelivered(OZON_dataProcessing.groupByOfferId(ozonService.getDetailReport(date)
                .getResult().getRows()));
    }

    public Map<String, Double> getMapSumReturn(@NotNull String apiKey,
                                                     @NotNull String clientId,
                                                     @NotNull String date){
        ozonService.setHeader(apiKey, clientId);

        return OZON_dataProcessing.sumReturn(OZON_dataProcessing.groupByOfferId(ozonService.getDetailReport(date)
                .getResult().getRows()));
    }

    public Map<String, Double> getMapSalesCommission(@NotNull String apiKey,
                                              @NotNull String clientId,
                                              @NotNull String date){
        ozonService.setHeader(apiKey, clientId);

        return OZON_dataProcessing.sumSalesCommission(OZON_dataProcessing.groupByOfferId(ozonService.getDetailReport(date)
                .getResult().getRows()));
    }

    public Map<String, Double> getMapLastMile(@NotNull String apiKey,
                                               @NotNull String clientId,
                                               @NotNull String date,
                                               @NotNull String from,
                                               @NotNull String to){
        ozonService.setHeader(apiKey, clientId);
        ArrayList<String> opTMile = new ArrayList<>();
        opTMile.add("OperationAgentDeliveredToCustomer");
        opTMile.add("OperationAgentStornoDeliveredToCustomer");
        opTMile.add("OperationReturnGoodsFBSofRMS");

        Map<String, List<Long>> offerSku = OZON_dataProcessing.getOfferSku(ozonService,date);

        OZON_TransactionReport request = ozonService.getTransactionReport(
                from, to,
                opTMile, "all",
                1,1000);
        List<OZON_TransactionReport.Operation> operations = new ArrayList<>();

        for(int i=1;i<=request.getResult().getPage_count();i++) {
            operations.addAll(ozonService.getTransactionReport(
                            from, to,
                            opTMile, "all",i,1000)
                    .getResult().getOperations());
        }

        return OZON_dataProcessing.sumLastMile(offerSku,operations);
    }

    public Map<String, Double> getMapAcquiring(@NotNull String apiKey,
                                               @NotNull String clientId,
                                               @NotNull String date,
                                               @NotNull String from,
                                               @NotNull String to){
        ozonService.setHeader(apiKey, clientId);
        ArrayList<String> opTAq = new ArrayList<>();
        opTAq.add("MarketplaceRedistributionOfAcquiringOperation");

        Map<String, List<Long>> offerSku = OZON_dataProcessing.getOfferSku(ozonService,date);

        OZON_TransactionReport request = ozonService.getTransactionReport(
                        from, to,
                        opTAq, "all",
                1,1000);
        List<OZON_TransactionReport.Operation> operations = new ArrayList<>();

        for(int i=1;i<=request.getResult().getPage_count();i++) {
            operations.addAll(ozonService.getTransactionReport(
                            from, to,
                            opTAq, "all",i,1000)
                    .getResult().getOperations());
        }

        return OZON_dataProcessing.sumAcquiring(offerSku,operations);
    }

    public Map<String, Double> getMapReturnDelivery(@NotNull String apiKey,
                                               @NotNull String clientId,
                                               @NotNull String date,
                                               @NotNull String from,
                                               @NotNull String to){
        ozonService.setHeader(apiKey, clientId);
        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationReturnGoodsFBSofRMS");

        Map<String, List<Long>> offerSku = OZON_dataProcessing.getOfferSku(ozonService,date);

        OZON_TransactionReport request = ozonService.getTransactionReport(
                from, to,
                opT, "all",
                1,1000);

        List<OZON_TransactionReport.Operation> operations = new ArrayList<>();

        for(int i=1;i<=request.getResult().getPage_count();i++) {
            operations.addAll(ozonService.getTransactionReport(
                            from, to,
                            opT, "all",i,1000)
                    .getResult().getOperations());
        }

        return OZON_dataProcessing.sumReturnDelivery(offerSku,operations);
    }

    public Map<String, Double> getMapReturnProcessing(@NotNull String apiKey,
                                                    @NotNull String clientId,
                                                    @NotNull String date,
                                                    @NotNull String from,
                                                    @NotNull String to){
        ozonService.setHeader(apiKey, clientId);
        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        Map<String, List<Long>> offerSku = OZON_dataProcessing.getOfferSku(ozonService,date);

        OZON_TransactionReport request = ozonService.getTransactionReport(
                from, to,
                opT, "all",
                1,1000);

        List<OZON_TransactionReport.Operation> operations = new ArrayList<>();

        for(int i=1;i<=request.getResult().getPage_count();i++) {
            operations.addAll(ozonService.getTransactionReport(
                            from, to,
                            opT, "all",i,1000)
                    .getResult().getOperations());
        }

        return OZON_dataProcessing.sumReturnProcessing(offerSku,operations);
    }

    public Map<String, Double> getMapShipmentProcessing(@NotNull String apiKey,
                                                      @NotNull String clientId,
                                                      @NotNull String date,
                                                      @NotNull String from,
                                                      @NotNull String to){
        ozonService.setHeader(apiKey, clientId);
        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        Map<String, List<Long>> offerSku = OZON_dataProcessing.getOfferSku(ozonService,date);

        OZON_TransactionReport request = ozonService.getTransactionReport(
                from, to,
                opT, "all",
                1,1000);

        List<OZON_TransactionReport.Operation> operations = new ArrayList<>();

        for(int i=1;i<=request.getResult().getPage_count();i++) {
            operations.addAll(ozonService.getTransactionReport(
                            from, to,
                            opT, "all",i,1000)
                    .getResult().getOperations());
        }

        return OZON_dataProcessing.sumShipmentProcessing(offerSku,operations);
    }

    public Map<String, Double> getMapLogistic(@NotNull String apiKey,
                                                        @NotNull String clientId,
                                                        @NotNull String date,
                                                        @NotNull String from,
                                                        @NotNull String to){
        ozonService.setHeader(apiKey, clientId);
        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        Map<String, List<Long>> offerSku = OZON_dataProcessing.getOfferSku(ozonService,date);

        OZON_TransactionReport request = ozonService.getTransactionReport(
                from, to,
                opT, "all",
                1,1000);

        List<OZON_TransactionReport.Operation> operations = new ArrayList<>();

        for(int i=1;i<=request.getResult().getPage_count();i++) {
            operations.addAll(ozonService.getTransactionReport(
                            from, to,
                            opT, "all",i,1000)
                    .getResult().getOperations());
        }

        return OZON_dataProcessing.sumLogistic(offerSku,operations);
    }
}
