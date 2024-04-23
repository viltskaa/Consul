package com.example.consul.api;

import com.example.consul.dto.OZON.OZON_PerformanceCampaigns;
import com.example.consul.dto.OZON.OZON_PerformanceTokenResult;
import com.example.consul.dto.OZON.OZON_campaignProductsInfo;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import com.example.consul.api.utils.Link;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class OZON_PerformanceApi {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    private void setHeaders() {
        headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
    }

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

    public OZON_PerformanceCampaigns getCampaigns(@NotNull String token,
                                                  @NotNull String dateFrom,
                                                  @NotNull String dateTo) {
        String url = "https://performance.ozon.ru:443/api/client/statistics/campaign/product/json";
        setHeaders();

        Map<String, String> map = new HashMap<>();

        map.put("dateFrom", dateFrom);
        map.put("dateTo", dateTo);
        map.put("campaigns", "[]");

        headers.setBearerAuth(token);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(url, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_PerformanceCampaigns.class);
        } else {
            return null;
        }
    }

    public OZON_campaignProductsInfo getCampaignInfo(@NotNull String token,
                                                     @NotNull Integer campaignId) {
        String url = Link.create("https://performance.ozon.ru:443/api/client/campaign/<arg>/v2/products")
                .setArgs(campaignId.toString())
                .build();
        setHeaders();

        headers.setBearerAuth(token);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(url, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_campaignProductsInfo.class);
        } else {
            return null;
        }
    }
}
