package com.example.consul.api;

import com.example.consul.api.utils.Link;
import com.example.consul.api.utils.YANDEX.*;
import com.example.consul.dto.YANDEX.YANDEX_CreateReport;
import com.example.consul.dto.YANDEX.YANDEX_ReportInfo;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class YANDEX_Api {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    public YANDEX_Api() {
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public void setHeaders(@NotNull String auth) {
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer y0_AgAAAABzBvISAAu7EwAAAAED4UtWAAAtEQmj-qVJyrHP6B9zqdC6RMWeeA");
//        headers.add("Authorization", "Bearer " + auth);
        headers.add("Content-Type", "application/json");
    }

    /**
     * Отчет по стоимости услуг
     * @param businessId (5731759) Идентификатор бизнеса.
     * @param dateFrom Начало периода, включительно.
     * @param dateTo Конец периода, включительно. Максимальный период — 1 год.
     * @param placementPrograms Список моделей (которым работает магазин), которые нужны в отчете.
     * @return url для загрузки файла
     */
    @Nullable
    public String getServicesReport(@NotNull Long businessId,
                                    @NotNull String dateFrom,
                                    @NotNull String dateTo,
                                    @NotNull List<YANDEX_PlacementType> placementPrograms) {
        final String createServicesReportUrl = "https://api.partner.market.yandex.ru/reports/united-marketplace-services/generate?format=FILE&language=RU";

        HttpEntity<String> request = new HttpEntity<>(new Gson()
                .toJson(new YANDEX_CreateServicesReportBody(businessId, dateFrom, dateTo, placementPrograms)), headers);

        return getDownloadUrl(createServicesReportUrl, request);
    }

    /**
     * Отчет по реализации
     * @param campaignId (23761421) Идентификатор кампании.
     * @param year Год.
     * @param month Месяц.
     * @return url для загрузки файла
     */
    @Nullable
    public String getRealizationReport(@NotNull Long campaignId,
                                       int year,
                                       int month) {
        final String createRealizationReportUrl = "https://api.partner.market.yandex.ru/reports/goods-realization/generate?format=FILE";

        HttpEntity<String> request = new HttpEntity<>(new Gson()
                .toJson(new YANDEX_CreateRealizationReportBody(campaignId, year, month)), headers);

        return getDownloadUrl(createRealizationReportUrl, request);
    }

    @Nullable
    private String getDownloadUrl(String url, HttpEntity<String> request){
        ResponseEntity<String> response = restTemplate
                .postForEntity(url, request, String.class);

        YANDEX_CreateReport createResponse = new Gson().fromJson(response.getBody(),
                YANDEX_CreateReport.class);

        if (createResponse.getStatus().equals(YANDEX_ApiResponseStatusType.OK)){
            return asyncGetDownloadUrl(createResponse.getReportId(), createResponse.getCreationTime());
        }
        else return null;
    }

    @Nullable
    private String asyncGetDownloadUrl(@NotNull String reportId,
                                       @NotNull Long creationTime) {
        final Link reportStatusUrl = Link
                .create("https://api.partner.market.yandex.ru/reports/info/<arg>")
                .setArgs(reportId);

        // пока пусть будет так
        try {
            Thread getUrlThread = new Thread(() -> {
                try {
                    Thread.sleep(creationTime + 100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            getUrlThread.start();
            getUrlThread.join();
        } catch (Exception exception) {
            return "Exception!" + exception.getMessage();
        }

        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                reportStatusUrl.build(),
                HttpMethod.GET,
                request,
                String.class
        );

        YANDEX_ReportInfo reportInfo = new Gson().fromJson(response.getBody(),
                YANDEX_ReportInfo.class);

        if(reportInfo.getReportStatus().equals(YANDEX_ReportStatusType.DONE))
            return reportInfo.getFileUrl();

        return null;
    }
}
