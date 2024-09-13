package com.example.consul.services;

import com.example.consul.api.OZON_Api;
import com.example.consul.api.OZON_PerformanceApi;
import com.example.consul.components.OZON_DataCreator;
import com.example.consul.conditions.ConditionalWithDelayChecker;
import com.example.consul.document.ExcelBuilder;
import com.example.consul.document.configurations.ExcelConfig;
import com.example.consul.document.configurations.HeaderConfig;
import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.document.models.ReportFile;
import com.example.consul.dto.OZON.*;
import com.example.consul.mapping.OZON_dataProcessing;
import com.example.consul.utils.Clustering;
import org.antlr.v4.runtime.misc.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
public class OZON_Service {
    private final OZON_Api ozonApi;
    private final OZON_DataCreator ozonExcelCreator;
    private final OZON_PerformanceApi ozonPerformanceApi;
    private final Map<String, OZON_PerformanceTokenExpires> performanceKey = new HashMap<>();
    private final ConditionalWithDelayChecker reportChecker;
    private final Clustering clustering;

    public OZON_Service(OZON_Api ozonApi,
                        OZON_DataCreator ozonExcelCreator,
                        OZON_PerformanceApi ozonPerformanceApi,
                        ConditionalWithDelayChecker reportChecker, Clustering clustering) {
        this.ozonApi = ozonApi;
        this.ozonExcelCreator = ozonExcelCreator;
        this.ozonPerformanceApi = ozonPerformanceApi;
        this.reportChecker = reportChecker;
        this.clustering = clustering;
    }

    public boolean isTokenNonExpired(@NotNull OZON_PerformanceTokenExpires token) {
        return Instant.now().getEpochSecond() <= token.getExpires_in();
    }

    public ReportFile createReport(@NotNull String apiKey,
                                   @NotNull String clientId,
                                   @NotNull String performanceClientId,
                                   @NotNull String performanceClientSecret,
                                   @NotNull Integer year,
                                   @NotNull Integer month) {
        List<OZON_TableRow> data = getData(
                apiKey,
                clientId,
                performanceClientId,
                performanceClientSecret,
                year,
                month
        );

        Map<String, List<OZON_TableRow>> clusteredData = clustering.of(data);

        return ExcelBuilder.createDocumentToReportFile(
                ExcelConfig.<OZON_TableRow>builder()
                        .fileName("report_ozon" + clientId + "_" + month + "_" + year + ".xls")
                        .header(
                                HeaderConfig.builder()
                                        .title("OZON")
                                        .description("NEW METHOD")
                                        .build()
                        )
                        .data(clusteredData.values().stream().toList())
                        .sheetsName(clusteredData.keySet().stream().toList())
                        .build()
        );
    }

    @Deprecated
    public List<OZON_TableRow> mergeMapsToTableRows(@NotNull String apiKey,
                                                    @NotNull String clientId,
                                                    @NotNull String performanceClientId,
                                                    @NotNull String performanceClientSecret,
                                                    @NotNull Integer year,
                                                    @NotNull Integer month) {
        List<String> opT = Stream.of(
                OZON_TransactionType.OperationAgentDeliveredToCustomer,
                OZON_TransactionType.OperationAgentStornoDeliveredToCustomer,
                OZON_TransactionType.OperationReturnGoodsFBSofRMS,
                OZON_TransactionType.MarketplaceRedistributionOfAcquiringOperation,
                OZON_TransactionType.OperationMarketplacePremiumSubscribtion,
                OZON_TransactionType.MarketplaceMarketingActionCostOperation
        ).map(Object::toString).toList();

        ozonApi.setHeaders(apiKey, clientId);
        getPerformanceToken(performanceClientId, performanceClientSecret);

        Pair<String, String> pairDate = ozonExcelCreator.getStartAndEndDateToUtc(month, year);

        OZON_TransactionReport ozonTransactionReport = getTransactionReport(
                pairDate.a,
                pairDate.b,
                opT,
                OZON_TransactionType.all.toString()
        );

        OZON_FinanceReport ozonFinanceReport = getFinanceReport(
                pairDate.a,
                pairDate.b
        );

        String[] offerIds = getListOfferIdByDate(month, year);

        return ozonExcelCreator.mergeMapsToTableRows(
                getDetailReport(month, year),
                getProductInfoByOfferId(offerIds),
                ozonTransactionReport,
                scheduledGetPerformanceReport(performanceClientId, performanceClientSecret, year, month),
                ozonFinanceReport
        );
    }

    public Pair<String, String> getDate(@NotNull Integer year,
                                        @NotNull Integer month){
        return ozonExcelCreator.getStartAndEndDateToUtc(month, year);
    }

    public void setHeaders(@NotNull String apiKey,
                           @NotNull String clientId){
        ozonApi.setHeaders(apiKey, clientId);
    }

    public List<OZON_TableRow> getData(@NotNull String apiKey,
                                       @NotNull String clientId,
                                       @NotNull String performanceClientId,
                                       @NotNull String performanceClientSecret,
                                       @NotNull Integer year,
                                       @NotNull Integer month) {
        Pair<String, String> pairDate = ozonExcelCreator.getStartAndEndDateToUtc(month, year);
        List<String> operationsType = Stream.of(
                OZON_TransactionType.OperationAgentDeliveredToCustomer,
                OZON_TransactionType.OperationAgentStornoDeliveredToCustomer,
                OZON_TransactionType.OperationReturnGoodsFBSofRMS,
                OZON_TransactionType.MarketplaceRedistributionOfAcquiringOperation
        ).map(Object::toString).toList();

        ozonApi.setHeaders(apiKey, clientId);

        CompletableFuture<OZON_DetailReport> detailReportCompletableFuture = CompletableFuture
                .supplyAsync(() -> getDetailReport(month, year));

        CompletableFuture<OZON_SkuProductsReport> ozonSkuProductsReportCompletableFuture = CompletableFuture
                .supplyAsync(() -> getListOfferIdByDate(month, year))
                .thenApplyAsync(this::getProductInfoByOfferId);

        CompletableFuture<OZON_TransactionReport> ozonTransactionReportCompletableFuture = CompletableFuture
                .supplyAsync(() -> getTransactionReport(
                        pairDate.a,
                        pairDate.b,
                        operationsType,
                        OZON_TransactionType.all.toString()
                ));

        CompletableFuture<OZON_FinanceReport> ozonFinanceReportCompletableFuture = CompletableFuture
                .supplyAsync(() -> getFinanceReport(
                        pairDate.a,
                        pairDate.b
                ));

        CompletableFuture<List<OZON_PerformanceReport>> ozonPerformanceReportCompletableFuture = CompletableFuture
                .supplyAsync(() -> scheduledGetPerformanceReport(
                        performanceClientId,
                        performanceClientSecret,
                        year,
                        month
                ));

        return ozonExcelCreator.mergeMapsToTableRows(
                detailReportCompletableFuture.join(),
                ozonSkuProductsReportCompletableFuture.join(),
                ozonTransactionReportCompletableFuture.join(),
                ozonPerformanceReportCompletableFuture.join(),
                ozonFinanceReportCompletableFuture.join()
        );
    }

    public OZON_TransactionReport getTransactionReport(@NotNull String from,
                                                       @NotNull String to,
                                                       @NotNull List<String> operation_type,
                                                       @NotNull String transaction_type,
                                                       int page,
                                                       int page_size) {
        try {
            return ozonApi.getTransactionReport(from, to, operation_type, transaction_type, page, page_size);
        } catch (NullPointerException exception) {
            return null;
        }
    }

    public OZON_FinanceReport getFinanceReport(@NotNull String from,
                                               @NotNull String to,
                                               @NotNull Boolean withDetails,
                                               @NotNull Integer page,
                                               @NotNull Integer pageSize) {
        try {
            return ozonApi.getFinanceReport(from, to, withDetails, page, pageSize);
        } catch (NullPointerException exception) {
            return null;
        }
    }

    public OZON_FinanceReport getFinanceReport(@NotNull String from,
                                               @NotNull String to) {
        final int pageSize = 1000;
        OZON_FinanceReport ozonFinanceReport = getFinanceReport(
                from,
                to,
                true,
                1,
                pageSize
        );

        for (int i = 2; i <= ozonFinanceReport.getResult().getPageCount(); i++) {
            OZON_FinanceReport ozonFinanceReportAdditional = getFinanceReport(
                    from,
                    to,
                    true,
                    i,
                    pageSize
            );

            ozonFinanceReport.getResult().getDetails().addAll(
                    ozonFinanceReportAdditional.getResult().getDetails()
            );
        }

        return ozonFinanceReport;
    }

    public OZON_TransactionReport getTransactionReport(@NotNull String from,
                                                       @NotNull String to,
                                                       @NotNull List<String> operation_type,
                                                       @NotNull String transaction_type) {

        final int page_size = 1000;
        OZON_TransactionReport ozonTransactionReport = getTransactionReport(
                from,
                to,
                operation_type,
                transaction_type,
                1,
                page_size
        );

        for (int i = 2; i <= ozonTransactionReport.getResult().getPageCount(); i++) {
            OZON_TransactionReport ozonTransactionReportAdditional = getTransactionReport(
                    from,
                    to,
                    operation_type,
                    transaction_type,
                    i,
                    page_size
            );

            ozonTransactionReport.getResult().getOperations().addAll(
                    ozonTransactionReportAdditional.getResult().getOperations()
            );
        }

        return ozonTransactionReport;
    }

    public OZON_DetailReport getDetailReport(@NotNull Integer month,
                                             @NotNull Integer year) {
        try {
            return ozonApi.getDetailReport(month, year);
        } catch (NullPointerException exception) {
            return null;
        }
    }

    public OZON_SkuProductsReport getProductInfoByOfferId(@NotNull String[] offerId) {
        try {
            return ozonApi.getProductInfoByOfferId(offerId);
        } catch (NullPointerException exception) {
            return null;
        }
    }

    public String[] getListOfferIdByDate(@NotNull Integer month,
                                         @NotNull Integer year) {
        OZON_DetailReport detailReport = getDetailReport(month, year);

        if (detailReport == null) {
            return new String[0];
        }

        if (detailReport.getResult() == null) {
            return new String[0];
        }

        return OZON_dataProcessing.groupByOfferId(detailReport.getResult().getRows())
                .keySet()
                .toArray(new String[0]);
    }

    public void getPerformanceToken(@NotNull String clientId,
                                    @NotNull String clientSecret) {
        if (!performanceKey.containsKey(clientId) ||
                (performanceKey.containsKey(clientId) && isTokenNonExpired(performanceKey.get(clientId)))) {
            OZON_PerformanceTokenResult token = ozonPerformanceApi.getToken(clientId, clientSecret);
            if (token == null) {
                return;
            }
            performanceKey.put(
                    clientId,
                    OZON_PerformanceTokenExpires.builder()
                            .access_token(token.getAccess_token())
                            .expires_in(Instant.now().getEpochSecond() + token.getExpires_in())
                            .build()
            );
        } else {
            performanceKey.get(clientId);
        }
    }

    public OZON_PerformanceCampaigns getCampaigns(@NotNull String clientId,
                                                  @NotNull String dateFrom,
                                                  @NotNull String dateTo) {
        if (performanceKey.containsKey(clientId) && isTokenNonExpired(performanceKey.get(clientId))) {
            return ozonPerformanceApi.getCampaigns(
                    performanceKey.get(clientId).getAccess_token(),
                    dateFrom,
                    dateTo);
        } else {
            return null;
        }
    }

    public OZON_PerformanceStatistic getPerformanceStatisticByCampaignId(@NotNull String clientId,
                                                                         @NotNull List<String> campaignId,
                                                                         @NotNull String dateFrom,
                                                                         @NotNull String dateTo) {
        if (campaignId.isEmpty()) {
            return new OZON_PerformanceStatistic();
        }

        if (performanceKey.containsKey(clientId) && isTokenNonExpired(performanceKey.get(clientId))) {
            return ozonPerformanceApi.getPerformanceStatisticByCampaignId(
                    performanceKey.get(clientId).getAccess_token(),
                    campaignId,
                    dateFrom,
                    dateTo);
        } else {
            return null;
        }
    }

    public OZON_PerformanceReportStatus getPerformanceReportStatusByUUID(@NotNull String clientId,
                                                                         @NotNull String UUID) {
        if (performanceKey.containsKey(clientId) && isTokenNonExpired(performanceKey.get(clientId))) {
            return ozonPerformanceApi.getPerformanceReportStatusByUUID(
                    performanceKey.get(clientId).getAccess_token(),
                    UUID
            );
        } else {
            System.out.println("getPerformanceReportStatusByUUID -- null");

            return null;
        }
    }

    public List<OZON_PerformanceReport> getPerformanceReportByUUID(@NotNull String clientId,
                                                                   @NotNull String UUID) {
        if (performanceKey.containsKey(clientId) && isTokenNonExpired(performanceKey.get(clientId))) {
            return ozonPerformanceApi.getPerformanceReportByUUID(
                    performanceKey.get(clientId).getAccess_token(),
                    UUID
            );
        } else {
            return null;
        }
    }

    public List<OZON_PerformanceReport> asyncGetPerformanceReportByUUID(@NotNull String clientId,
                                                                        @NotNull String UUID) {
        Thread reportStatusThread = new Thread(() -> {
            OZON_PerformanceReportStatus status;
            do {
                status = getPerformanceReportStatusByUUID(
                        clientId,
                        UUID
                );
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (!status.getState().equals(OZON_PerformanceReportStatus.State.OK));
        });
        reportStatusThread.start();
        try {
            reportStatusThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return getPerformanceReportByUUID(clientId, UUID);
    }

    public List<OZON_PerformanceReport> scheduledGetPerformanceReportByUUID(@NotNull String clientId,
                                                                            @NotNull String UUID) {
        Boolean value = reportChecker.start(() -> {
            OZON_PerformanceReportStatus status = getPerformanceReportStatusByUUID(
                    clientId,
                    UUID
            );

            System.out.println(status.getUUID());
            System.out.println(status.getState());
            //c3abcb2a-6431-4866-8cf3-8f63bbe292b1

            return status != null && status.getState().equals(OZON_PerformanceReportStatus.State.OK);
        }, 2L);

        if (value) {
            System.out.println("1");
            return getPerformanceReportByUUID(clientId, UUID);
        }
        System.out.println("2");
        return null;
    }

    public List<OZON_PerformanceReport> scheduledGetPerformanceReport(@NotNull String clientId,
                                                                      @NotNull String clientSecret,
                                                                      @NotNull Integer year,
                                                                      @NotNull Integer month) {
        Pair<String, String> dateEntry = ozonExcelCreator.getStartAndEndDateToDate(month, year);
        String dateFrom = dateEntry.a;
        String dateTo = dateEntry.b;

        getPerformanceToken(clientId, clientSecret);

        OZON_PerformanceCampaigns ozonPerformanceCampaigns = getCampaigns(clientId, dateFrom, dateTo);

        List<String> activeCampaignList = ozonPerformanceCampaigns.getRows()
                .stream().filter(x -> !"0".equals(x.getMoneySpent()))
                .map(OZON_PerformanceCampaigns.OZON_PerformanceCampaign::getId).toList();

        if (activeCampaignList.isEmpty()) {
            return new ArrayList<>();
        }

        OZON_PerformanceStatistic statistic = getPerformanceStatisticByCampaignId(
                clientId,
                activeCampaignList,
                dateFrom,
                dateTo
        );

        return scheduledGetPerformanceReportByUUID(
                clientId,
                statistic.getUUID()
        );
    }
}
