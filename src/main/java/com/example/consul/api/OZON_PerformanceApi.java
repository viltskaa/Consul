package com.example.consul.api;

import com.example.consul.dto.OZON_PerformanceCampaign;
import com.example.consul.dto.OZON_PerformanceTokenResult;
import org.jetbrains.annotations.NotNull;
import com.example.consul.api.utils.Link;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OZON_PerformanceApi {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    private void setHeaders() {
        headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.setContentType(MediaType.valueOf("text/csv; charset=UTF-8"));
    }

    public OZON_PerformanceTokenResult get_token(@NotNull String clientId,
                                                 @NotNull String clientSecret) {
        String url = "https://performance.ozon.ru/api/client/token";
        setHeaders();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<OZON_PerformanceTokenResult> response = restTemplate
                .postForEntity(url, request, OZON_PerformanceTokenResult.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public List<OZON_PerformanceCampaign> getCampaigns(@NotNull String dateFrom,
                                                       @NotNull String dateTo) {
        String url = "https://performance.ozon.ru:443/api/client/statistics/campaign/product/json";
        setHeaders();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add("dateFrom", dateFrom);
        map.add("dateTo", dateTo);
        map.add("campaigns", "[]");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<OZON_PerformanceCampaign[]> response = restTemplate
                .postForEntity(url, request, OZON_PerformanceCampaign[].class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Arrays.asList(
                    Objects.requireNonNull(response.getBody())
            );
        } else {
            return null;
        }
    }

    public Object getCampaignInfo(@NotNull Integer campaignId) {
        String url = Link.create("https://performance.ozon.ru:443/api/client/campaign/<arg>/v2/products")
                .setArgs(campaignId.toString())
                .build();
        setHeaders();


        return null;
    }
}
