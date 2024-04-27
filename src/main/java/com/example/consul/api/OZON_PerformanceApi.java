package com.example.consul.api;

import com.example.consul.api.utils.ForTransactions;
import com.example.consul.dto.OZON.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import com.example.consul.api.utils.Link;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OZON_PerformanceApi {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    private void setHeaders() {
        headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
    }

    @Nullable
    public OZON_PerformanceTokenResult getToken(@NotNull String clientId,
                                                @NotNull String clientSecret) {
        String url = "https://performance.ozon.ru/api/client/token";
        setHeaders();

        Map<String, String> map = new HashMap<>();

        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("grant_type", "client_credentials");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<OZON_PerformanceTokenResult> response = restTemplate
                .postForEntity(url, request, OZON_PerformanceTokenResult.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    @Nullable
    public OZON_PerformanceCampaigns getCampaigns(@NotNull String token,
                                                  @NotNull String dateFrom,
                                                  @NotNull String dateTo) {
        String url = Link
                .create("https://performance.ozon.ru:443/api/client/statistics/campaign/product/json?dateFrom=<arg>&dateTo=<arg>")
                .setArgs(dateFrom, dateTo).build();
        setHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.GET, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_PerformanceCampaigns.class);
        } else {
            return null;
        }
    }

    @Nullable
    public OZON_PerformanceStatistic getPerformanceStatisticByCampaignId(@NotNull String token,
                                                                         @NotNull List<String> campaignId,
                                                                         @NotNull String dateFrom,
                                                                         @NotNull String dateTo) {
        String url = "https://performance.ozon.ru:443/api/client/statistics/json";
        setHeaders();
        headers.setBearerAuth(token);

        Map<String, String> map = new HashMap<>();

        map.put("campaigns", String.valueOf(campaignId));
        map.put("dateFrom", dateFrom);
        map.put("dateTo", dateTo);
        map.put("groupBy", "START_OF_MONTH");

        OZON_PerformanceStatisticConfig config = OZON_PerformanceStatisticConfig
                .builder()
                .campaigns(campaignId)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .groupBy(OZON_PerformanceStatisticConfig.GroupBy.START_OF_MONTH)
                .build();

        HttpEntity<String> request = new HttpEntity<>
                (new Gson()
                        .toJson(config),
                        headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(url, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(
                    response.getBody(),
                    OZON_PerformanceStatistic.class
            );
        } else {
            return null;
        }
    }

    @Nullable
    public OZON_PerformanceReportStatus getPerformanceReportStatusByUUID(@NotNull String token,
                                                   @NotNull String UUID) {
        String url = Link.create("https://performance.ozon.ru:443/api/client/statistics/<arg>")
                .setArgs(UUID).build();
        setHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(
                    response.getBody(),
                    OZON_PerformanceReportStatus.class
            );
        }
        else {
            return null;
        }
    }

    @Nullable
    public List<OZON_PerformanceReport> getPerformanceReportByUUID(@NotNull String token,
                                                                   @NotNull String UUID) {
        String url = Link.create("https://performance.ozon.ru:443/api/client/statistics/report?UUID=<arg>")
                .setArgs(UUID)
                .build();
        setHeaders();

        headers.setBearerAuth(token);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.GET, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Type type = new TypeToken<LinkedTreeMap<String, Object>>() {
            }.getType();
            LinkedTreeMap<String, Object> test = new Gson().fromJson(response.getBody(), type);
            List<OZON_PerformanceReport> reports = new ArrayList<>();

            for (String key : test.keySet()) {
                ObjectMapper objectMapper = new ObjectMapper();
                OZON_PerformanceReport report = objectMapper
                        .convertValue(test.get(key), new TypeReference<>() {
                        });
                report.setId(key);
                reports.add(report);
            }

            return reports;
        } else {
            return null;
        }
    }
}
