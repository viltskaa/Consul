package com.example.consul.api;

import com.example.consul.api.utils.Link;
import com.example.consul.dto.WB.WB_AdReport;
import com.example.consul.dto.WB.WB_DetailReport;
import com.example.consul.dto.WB.WB_SaleReport;
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
    public List<WB_DetailReport> getDetailReportV1(@NotNull String dateFrom,
                                                   @NotNull String dateTo) {
        return getDetailReport(dateFrom, dateTo, 0L, "v1");
    }

    @Nullable
    public List<WB_DetailReport> getDetailReportV5(@NotNull String dateFrom,
                                                   @NotNull String dateTo) {
        return getDetailReport(dateFrom, dateTo, 0L, "v5");
    }

    @Nullable
    public List<WB_DetailReport> getDetailReportWithOffsetV1(@NotNull String dateFrom,
                                                             @NotNull String dateTo,
                                                             @NotNull Long rrdId) {
        return getDetailReport(dateFrom, dateTo, rrdId, "v1");
    }

    @Nullable
    public List<WB_DetailReport> getDetailReportWithOffsetV5(@NotNull String dateFrom,
                                                         @NotNull String dateTo,
                                                         @NotNull Long rrdId) {
        return getDetailReport(dateFrom, dateTo, rrdId, "v5");
    }

    @Nullable
    public List<WB_SaleReport> getSaleReport(@NotNull String dateFrom) {
        if (dateFrom.isEmpty())
            return null;

        final String saleReportUrl = "https://statistics-api.wildberries.ru/api/v1/supplier/sales?dateFrom=%s&flag=%s"
                .formatted(dateFrom, 1);

        HttpEntity<WB_SaleReport[]> request = new HttpEntity<>(headers);
        ResponseEntity<WB_SaleReport[]> response = restTemplate
                .exchange(saleReportUrl, HttpMethod.GET, request, WB_SaleReport[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            return null;
        }
    }

    @Nullable
    private List<WB_DetailReport> getDetailReport(@NotNull String dateFrom,
                                                  @NotNull String dateTo,
                                                  @NotNull Long rrdid,
                                                  @NotNull String version) {
        if (dateTo.isEmpty() || dateFrom.isEmpty()) return null;

        final String detailReportUrl = "https://statistics-api.wildberries.ru/api/%s/supplier/reportDetailByPeriod?dateFrom=%s&dateTo=%s&rrdid=%s"
                        .formatted(version, dateFrom, dateTo, rrdid);

        HttpEntity<WB_DetailReport[]> request = new HttpEntity<>(headers);
        ResponseEntity<WB_DetailReport[]> response = restTemplate
                .exchange(detailReportUrl, HttpMethod.GET, request, WB_DetailReport[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            return null;
        }
    }

    @Nullable
    public List<WB_AdReport> getAdReport(@NotNull String dateFrom,
                                         @NotNull String dateTo) throws NullPointerException {
        if (dateTo.isEmpty() || dateFrom.isEmpty()) return null;

        final Link adReportUrl = Link.create(
                "https://advert-api.wb.ru/adv/v1/upd?from=<arg>&to=<arg>");

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
