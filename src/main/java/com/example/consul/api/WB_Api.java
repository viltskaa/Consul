package com.example.consul.api;

import com.example.consul.api.utils.Link;
import com.example.consul.dto.WB_AdReport;
import com.example.consul.dto.WB_DetailReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class WB_Api {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    private final Link detailReportUrl = Link.create(
            "https://statistics-api.wildberries.ru/api/<arg>/supplier/reportDetailByPeriod?dateFrom=<arg>&dateTo=<arg>");
    private final Link adReportUrl = Link.create(
            "https://advert-api.wb.ru/adv/v1/upd?from=<arg>&to=<arg>");

    public WB_Api() {
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public void setApiKey(@NotNull String apiKey) {
        headers = new HttpHeaders();
        headers.add("Authorization", apiKey);
        headers.add("Accept", "application/json");
        headers.setContentType(MediaType.valueOf("text/csv; charset=UTF-8"));
    }

    @Nullable
    public List<WB_DetailReport> getDetailReport(@NotNull String dateFrom,
                                                 @NotNull String dateTo) throws NullPointerException {
        if (dateTo.isEmpty() || dateFrom.isEmpty()) return null;

        HttpEntity<WB_DetailReport[]> request = new HttpEntity<>(headers);
        ResponseEntity<WB_DetailReport[]> response = restTemplate
                .exchange(detailReportUrl.setArgs("v1",dateFrom, dateTo).build(),
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

        String url = adReportUrl.setArgs(dateFrom, dateTo).build();
        HttpEntity<WB_AdReport[]> request = new HttpEntity<>(headers);
        ResponseEntity<WB_AdReport[]> response = restTemplate
                .exchange(url,
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
