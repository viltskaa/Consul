package com.example.consul.services;

import com.example.consul.api.OZON_Api;
import com.example.consul.api.OZON_PerformanceApi;
import com.example.consul.conditions.ReportChecker;
import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.dto.OZON.*;
import com.example.consul.mapping.OZON_dataProcessing;
import org.antlr.v4.runtime.misc.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class OZON_Service {
    private final OZON_Api ozonApi;
    private final OZON_ExcelCreator ozonExcelCreator;
    private final OZON_PerformanceApi ozonPerformanceApi;
    private final Map<String, OZON_PerformanceTokenExpires> performanceKey = new HashMap<>();
    private final ReportChecker reportChecker;

    public OZON_Service(OZON_Api ozonApi,
                        OZON_ExcelCreator ozonExcelCreator,
                        OZON_PerformanceApi ozonPerformanceApi,
                        ReportChecker reportChecker) {
        this.ozonApi = ozonApi;
        this.ozonExcelCreator = ozonExcelCreator;
        this.ozonPerformanceApi = ozonPerformanceApi;
        this.reportChecker = reportChecker;
    }

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
                OZON_TransactionType.MarketplaceRedistributionOfAcquiringOperation
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

        String[] offerIds = getListOfferIdByDate(month, year);

        return ozonExcelCreator.mergeMapsToTableRows(
                getDetailReport(month, year),
                getProductInfoByOfferId(offerIds),
                ozonTransactionReport,
                scheduledGetPerformanceReport(performanceClientId, performanceClientSecret, year, month)
        );
    }

    public void setHeader(@NotNull String apiKey, @NotNull String clientId) {
        ozonApi.setHeaders(apiKey, clientId);
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

        for (int i = 2; i <= ozonTransactionReport.getResult().getPage_count(); i++) {
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
                (performanceKey.containsKey(clientId) && !performanceKey.get(clientId).isExpired())) {
            OZON_PerformanceTokenResult token = ozonPerformanceApi.getToken(clientId, clientSecret);
            if (token == null) {
                return;
            }
            performanceKey.put(
                    clientId, OZON_PerformanceTokenExpires.of(token)
            );
        } else {
            performanceKey.get(clientId);
        }
    }

    public OZON_PerformanceCampaigns getCampaigns(@NotNull String clientId,
                                                  @NotNull String dateFrom,
                                                  @NotNull String dateTo) {
        if (performanceKey.containsKey(clientId) && !performanceKey.get(clientId).isExpired()) {
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

        if (performanceKey.containsKey(clientId) && !performanceKey.get(clientId).isExpired()) {
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
        if (performanceKey.containsKey(clientId) && !performanceKey.get(clientId).isExpired()) {
            return ozonPerformanceApi.getPerformanceReportStatusByUUID(
                    performanceKey.get(clientId).getAccess_token(),
                    UUID
            );
        } else {
            return null;
        }
    }

    public List<OZON_PerformanceReport> getPerformanceReportByUUID(@NotNull String clientId,
                                                                   @NotNull String UUID) {
        if (performanceKey.containsKey(clientId) && !performanceKey.get(clientId).isExpired()) {
            return ozonPerformanceApi.getPerformanceReportByUUID(
                    performanceKey.get(clientId).getAccess_token(),
                    UUID
            );
        } else {
            return null;
        }
    }

    public List<OZON_PerformanceReport> scheduledGetPerformanceReportByUUID(@NotNull String clientId,
                                                                            @NotNull String UUID) {
        reportChecker.init(() -> {
            OZON_PerformanceReportStatus status = getPerformanceReportStatusByUUID(
                    clientId,
                    UUID
            );
            if (status == null) {
                return false;
            }
            return status.getState().equals(OZON_PerformanceReportStatus.State.OK);
        });
        if (reportChecker.start(2L)) {
            return getPerformanceReportByUUID(clientId, UUID);
        }
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
            return null;
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
