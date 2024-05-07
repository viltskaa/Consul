package com.example.consul.services;

import com.example.consul.api.OZON_Api;
import com.example.consul.api.OZON_PerformanceApi;
import com.example.consul.conditions.ReportChecker;
import com.example.consul.dto.OZON.*;
import com.example.consul.mapping.OZON_dataProcessing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OZON_Service {
    private final OZON_Api ozonApi;
    private final OZON_PerformanceApi ozonPerformanceApi;
    // key - client_id
    private final Map<String, OZON_PerformanceTokenExpires> performanceKey = new HashMap<>();
    private final ReportChecker reportChecker;

    public OZON_Service(OZON_Api ozonApi,
                        OZON_PerformanceApi ozonPerformanceApi,
                        ReportChecker reportChecker) {
        this.ozonApi = ozonApi;
        this.ozonPerformanceApi = ozonPerformanceApi;
        this.reportChecker = reportChecker;
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
        return OZON_dataProcessing
                .groupByOfferId(ozonApi.getDetailReport(month, year)
                        .getResult().getRows())
                .keySet()
                .toArray(new String[0]);
    }

    public OZON_SkuProductsReport getProductInfo(@NotNull List<Long> skus) {
        try {
            return ozonApi.getProductInfo(skus);
        } catch (NullPointerException exception) {
            return null;
        }
    }

    public String getPerformanceToken(@NotNull String clientId,
                                      @NotNull String clientSecret) {
        if (!performanceKey.containsKey(clientId) ||
                (performanceKey.containsKey(clientId) && !performanceKey.get(clientId).isExpired())) {
            OZON_PerformanceTokenResult token = ozonPerformanceApi.getToken(clientId, clientSecret);
            if (token == null) {
                return null;
            }
            performanceKey.put(
                    clientId, OZON_PerformanceTokenExpires.of(token)
            );
            return token.getAccess_token();
        } else {
            return performanceKey.get(clientId).getAccess_token();
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

    @Nullable
    public List<OZON_PerformanceReport> asyncGetPerformanceReportByUUID(@NotNull String clientId,
                                                                        @NotNull String UUID) {
        try {
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
            reportStatusThread.join();
        } catch (Exception exception) {
            return null;
        }

        return getPerformanceReportByUUID(clientId, UUID);
    }

    public List<OZON_PerformanceReport> scheduledGetPerformanceReportByUUID(@NotNull String clientId,
                                                                            @NotNull String UUID) {
        reportChecker.init(() -> {
            OZON_PerformanceReportStatus status = getPerformanceReportStatusByUUID(
                    clientId,
                    UUID
            );
            System.out.println(status.getState());
            return status.getState().equals(OZON_PerformanceReportStatus.State.OK);
        });
        if (reportChecker.start(2L)) {
            return getPerformanceReportByUUID(clientId, UUID);
        }
        return null;
    }
}
