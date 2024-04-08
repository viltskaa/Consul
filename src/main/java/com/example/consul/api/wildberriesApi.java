package com.example.consul.api;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class wildberriesApi {
    private final HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    private final link detailReportUrl = link.create(
            "https://statistics-api.wildberries.ru/api/v3/supplier/reportDetailByPeriod?dateFrom=<arg>&dateTo=<arg>");
    private final link adReportUrl = link.create(
            "https://advert-api.wb.ru/adv/v1/upd?dateFrom=<arg>&dateTo=<arg>");

    public wildberriesApi(@NotNull String apiKey) {
        headers.add("Authorization", apiKey);
        headers.add("Accept", "application/json");
    }

    @Nullable
    public Object[] getDetailReport(@NotNull String dateFrom,
                                    @NotNull String dateTo) {
        if (dateTo.isEmpty() || dateFrom.isEmpty()) return null;

        HttpEntity<Object[]> request = new HttpEntity<>(headers);
        ResponseEntity<Object[]> response = restTemplate
                .exchange(detailReportUrl.setArgs(
                                dateFrom, dateTo).build(),
                        HttpMethod.GET, request,
                        Object[].class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    @Nullable
    public Object[] getAdReport(@NotNull String dateFrom,
                                @NotNull String dateTo) {
        if (dateTo.isEmpty() || dateFrom.isEmpty()) return null;

        return null;
    }
}
