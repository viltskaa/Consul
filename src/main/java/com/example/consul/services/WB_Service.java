package com.example.consul.services;

import com.example.consul.api.WB_Api;
import com.example.consul.dto.WB_AdReport;
import com.example.consul.dto.WB_DetailReport;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WB_Service {
    private final WB_Api wbApi;

    public WB_Service(WB_Api wbApi) {
        this.wbApi = wbApi;
    }

    public void setApiKey(@NotNull String apiKey) {
        wbApi.setApiKey(apiKey);
    }

    public List<WB_DetailReport> getDetailReport(
            @NotNull String dateFrom,
            @NotNull String dateTo
    ) {
        try {
            return wbApi.getDetailReport(dateFrom, dateTo);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    public List<WB_AdReport> getAdReport(
            @NotNull String dateFrom,
            @NotNull String dateTo
    ) {
        try {
            return wbApi.getAdReport(dateFrom, dateTo);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }
}
