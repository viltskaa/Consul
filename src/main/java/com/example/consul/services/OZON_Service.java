package com.example.consul.services;

import com.example.consul.api.OZON_Api;
import com.example.consul.api.OZON_PerformanceApi;
import com.example.consul.dto.OZON.*;
import org.jetbrains.annotations.NotNull;
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

    public OZON_Service(OZON_Api ozonApi,
                        OZON_PerformanceApi ozonPerformanceApi) {
        this.ozonApi = ozonApi;
        this.ozonPerformanceApi = ozonPerformanceApi;
    }

    public void setHeader(@NotNull String apiKey, @NotNull String clientId) {
        ozonApi.setHeaders(apiKey, clientId);
    }

    public OZON_TransactionReport getTransactionReport(@NotNull String from,
                                                             @NotNull String to,
                                                             @NotNull ArrayList<String> operation_type,
                                                             @NotNull String transaction_type,
                                                       int page,
                                                       int page_size) {
        try {
            return ozonApi.getTransactionReport(from, to, operation_type, transaction_type,page,page_size);
        } catch (NullPointerException exception) {
            return null;
        }
    }

    public OZON_DetailReport getDetailReport(@NotNull String date) {
        try {
            return ozonApi.getDetailReport(date);
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

    public OZON_SkuProductsReport getProductInfo(@NotNull List<Long> skus) {
        try {
            return ozonApi.getProductInfo(skus);
        } catch (NullPointerException exception) {
            return null;
        }
    }

    public String getPerformanceToken(@NotNull String clientId,
                                      @NotNull String clientSecret) {
        try {
            if (!performanceKey.containsKey(clientId) ||
                    (performanceKey.containsKey(clientId) && !performanceKey.get(clientId).isExpired())) {
                OZON_PerformanceTokenResult token = ozonPerformanceApi.getToken(clientId, clientSecret);
                performanceKey.put(
                        clientId, OZON_PerformanceTokenExpires.of(token)
                );
                return token.getAccess_token();
            } else {
                return performanceKey.get(clientId).getAccess_token();
            }
        } catch (Exception exception) {
            return "error";
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
}
