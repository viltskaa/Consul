package com.example.consul.services;

import com.example.consul.api.OZON_Api;
import com.example.consul.dto.OZON_TransactionReport;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OZON_Service {
    private final OZON_Api ozonApi;

    public OZON_Service(OZON_Api ozonApi) {
        this.ozonApi = ozonApi;
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
}
