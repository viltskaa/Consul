package com.example.consul.services;

import com.example.consul.api.OZON_Api;
import com.example.consul.api.OZON_PerformanceApi;
import com.example.consul.dto.OZON_TransactionReport;
import org.antlr.v4.runtime.misc.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OZON_Service {
    private final OZON_Api ozonApi;
    private final OZON_PerformanceApi ozonPerformanceApi;
    private Pair<Long, String> performanceKey = new Pair<>(0L, null);

    public OZON_Service(OZON_Api ozonApi,
                        OZON_PerformanceApi ozonPerformanceApi) {
        this.ozonApi = ozonApi;
        this.ozonPerformanceApi = ozonPerformanceApi;
    }

    public void setHeader(@NotNull String apiKey, @NotNull String clientId) {
        ozonApi.setHeaders(apiKey,clientId);
    }

    public List<OZON_TransactionReport> getTransactionReport(@NotNull String from,
                                                             @NotNull String to,
                                                             @NotNull ArrayList<String> operation_type,
                                                             @NotNull String transaction_type) {
        try {
            return ozonApi.getTransactionReport(from, to,operation_type,transaction_type);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    public String getPerformanceToken(@NotNull String clientId,
                                      @NotNull String clientSecret) {
        try {
            if (Instant.now().getEpochSecond() > performanceKey.a + 1800) {
                String token = ozonPerformanceApi.get_token(clientId, clientSecret).getAccess_token();
                performanceKey = new Pair<>(Instant.now().getEpochSecond(), token);
                return token;
            }
            else {
                return performanceKey.b;
            }
        } catch (Exception exception) {
            return "error";
        }
    }
}
