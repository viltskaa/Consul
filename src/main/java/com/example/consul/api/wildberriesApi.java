package com.example.consul.api;

import com.example.consul.dto.WB_AdReport;
import com.example.consul.dto.WB_DetailReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    public List<WB_DetailReport> getDetailReport(@NotNull String dateFrom,
                                                 @NotNull String dateTo) throws NullPointerException {
        if (dateTo.isEmpty() || dateFrom.isEmpty()) return null;

        HttpEntity<WB_DetailReport[]> request = new HttpEntity<>(headers);
        ResponseEntity<WB_DetailReport[]> response = restTemplate
                .exchange(detailReportUrl.setArgs(dateFrom, dateTo).build(),
                        HttpMethod.GET, request,
                        WB_DetailReport[].class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Arrays.asList(
                    Objects.requireNonNull(response.getBody())
            );
        } else {
            return null;
        }
    }

    @Nullable
    public List<WB_AdReport> getAdReport(@NotNull String dateFrom,
                                         @NotNull String dateTo) throws NullPointerException {
        if (dateTo.isEmpty() || dateFrom.isEmpty()) return null;

        HttpEntity<WB_AdReport[]> request = new HttpEntity<>(headers);
        ResponseEntity<WB_AdReport[]> response = restTemplate
                .exchange(detailReportUrl.setArgs(dateFrom, dateTo).build(),
                        HttpMethod.GET, request,
                        WB_AdReport[].class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Arrays.asList(
                    Objects.requireNonNull(response.getBody())
            );
        } else {
            return null;
        }
    }
}
