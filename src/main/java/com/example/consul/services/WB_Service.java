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

    public void setApi(@NotNull String apiKey) {
        wbApi.setApi(apiKey);
    }

    public List<WB_DetailReport> getDetailReport() {
        try {
            return wbApi.getDetailReport("2024-01-22", "2024-01-28");
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    public List<WB_AdReport> getAdReport() {
        try {
            return wbApi.getAdReport("2024-01-22", "2024-01-28");
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }
}
