package com.example.consul.services;

import com.example.consul.api.YANDEX_Api;
import com.example.consul.api.utils.YANDEX.YANDEX_ReportStatusType;
import com.example.consul.components.YANDEX_DataCreator;
import com.example.consul.conditions.ConditionalWithDelayChecker;
import com.example.consul.document.models.ReportFile;
import com.example.consul.document.models.YANDEX_TableRow;
import com.example.consul.document.v2.ExcelBuilderV2;
import com.example.consul.document.v2.models.Sheet;
import com.example.consul.document.v2.models.Table;
import com.example.consul.dto.YANDEX.YANDEX_CreateReport;
import com.example.consul.dto.YANDEX.YANDEX_ReportInfo;
import com.example.consul.utils.Clustering;
import org.antlr.v4.runtime.misc.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class YANDEX_Service {
    private final YANDEX_Api yandexApi;
    private final ConditionalWithDelayChecker reportChecker;
    private final Clustering clustering;
    private final YANDEX_DataCreator yandexDataCreator;

    public YANDEX_Service(YANDEX_Api api,
                          ConditionalWithDelayChecker reportChecker, Clustering clustering, YANDEX_DataCreator yandexDataCreator) {
        this.yandexApi = api;
        this.reportChecker = reportChecker;
        this.clustering = clustering;
        this.yandexDataCreator = yandexDataCreator;
    }

    public ReportFile createReport(@NotNull String auth,
                                   @NotNull Long campaignId,
                                   @NotNull Long businessId,
                                   int year,
                                   int month) {
        List<YANDEX_TableRow> data = getData(
                auth,
                campaignId,
                businessId,
                year,
                month
        );

        Map<String, List<YANDEX_TableRow>> clusteredData = clustering.of(data, "Нераспределенные");

        List<Sheet<YANDEX_TableRow>> listOfSheets = clusteredData.entrySet().stream()
                .map(entry -> Sheet.<YANDEX_TableRow>builder()
                        .name(entry.getKey())
                        .tables(Collections.singletonList(
                                Table.<YANDEX_TableRow>builder()
                                        .name(entry.getKey())
                                        .data(entry.getValue())
                                        .build()
                        ))
                        .build()
                )
                .toList();

        Sheet[] sheetsArray = listOfSheets.toArray(new Sheet[0]);


        return ExcelBuilderV2.<YANDEX_TableRow>builder()
                .setFilename("report_yandex.xlsx")
                .setSheets(sheetsArray)
                .build()
                .createDocument();
    }

    public YANDEX_CreateReport getServicesReport(@NotNull Long businessId,
                                                 @NotNull String dateFrom,
                                                 @NotNull String dateTo) {
        return yandexApi.createServicesReport(businessId, dateFrom, dateTo, new ArrayList<>());
    }

    public YANDEX_CreateReport getOrdersReport(@NotNull Long businessId,
                                               @NotNull String dateFrom,
                                               @NotNull String dateTo) {
        return yandexApi.createOrdersReport(businessId, dateFrom, dateTo);
    }

    public YANDEX_CreateReport getRealizationReport(@NotNull Long campaignId,
                                                    int year,
                                                    int month) {
        return yandexApi.createRealizationReport(campaignId, year, month);
    }

    public YANDEX_ReportInfo getReportInfo(@NotNull String reportId) {
        return yandexApi.getReportInfo(reportId);
    }

    public String scheduledGetServicesReport(@NotNull String auth,
                                             @NotNull Long businessId,
                                             @NotNull String dateFrom,
                                             @NotNull String dateTo) {
        yandexApi.setHeaders(auth);
        YANDEX_CreateReport report = getServicesReport(businessId, dateFrom, dateTo);
        Boolean value = reportChecker.start(() -> {
            YANDEX_ReportInfo info = getReportInfo(report.getReportId());
            return info != null && info.getReportStatus().equals(YANDEX_ReportStatusType.DONE);
        }, 2L);

        if (value)
            return getReportInfo(report.getReportId()).getFileUrl();

        return null;
    }

    public String scheduledGetOrdersReport(@NotNull String auth,
                                           @NotNull Long businessId,
                                           @NotNull String dateFrom,
                                           @NotNull String dateTo) {
        yandexApi.setHeaders(auth);
        YANDEX_CreateReport report = getOrdersReport(businessId, dateFrom, dateTo);
        Boolean value = reportChecker.start(() -> {
            YANDEX_ReportInfo info = getReportInfo(report.getReportId());
            return info != null && info.getReportStatus().equals(YANDEX_ReportStatusType.DONE);
        }, 2L);

        if (value)
            return getReportInfo(report.getReportId()).getFileUrl();

        return null;
    }

    public String scheduledGetRealizationReport(@NotNull String auth,
                                                @NotNull Long campaignId,
                                                int year,
                                                int month) {
        yandexApi.setHeaders(auth);
        YANDEX_CreateReport report = getRealizationReport(campaignId, year, month);
        Boolean value = checkReport(report.getReportId(), report.getCreationTime());

        if (value)
            return getReportInfo(report.getReportId()).getFileUrl();

        return null;
    }

    private Boolean checkReport(@NotNull String reportId,
                                @NotNull Long creationTime) {
        return reportChecker.start(() -> {
            YANDEX_ReportInfo info = getReportInfo(reportId);
            return info != null && info.getReportStatus().equals(YANDEX_ReportStatusType.DONE);
        }, creationTime / 1000);
    }

    public Pair<String, String> getStartAndEndDateToDate(Integer month, Integer year) {
        LocalDate date = LocalDate.of(year, month, 1);
        return new Pair<>(date.withDayOfMonth(1).toString(), date.withDayOfMonth(date.lengthOfMonth()).toString());
    }

    public List<YANDEX_TableRow> getData(@NotNull String auth,
                                         @NotNull Long campaignId,
                                         @NotNull Long businessId,
                                         int year,
                                         int month) {
        yandexApi.setHeaders(auth);
        Pair<String, String> date = getStartAndEndDateToDate(month, year);

        try {
            URL realizationUrl = new URL(scheduledGetRealizationReport(auth, campaignId, year, month));
            URL servicesUrl = new URL(scheduledGetServicesReport(auth, businessId, date.a, date.b));
            URL orderUrl = new URL(scheduledGetOrdersReport(auth, businessId, date.a, date.b));

            try (
                    InputStream realizationInputStream = realizationUrl.openStream();
                    InputStream servicesInputStream = servicesUrl.openStream();
                    InputStream ordersInputStream = orderUrl.openStream()
            ) {
                InputStream inputStreamRealization = new ByteArrayInputStream(realizationInputStream.readAllBytes());
                InputStream inputStreamServices = new ByteArrayInputStream(servicesInputStream.readAllBytes());
                InputStream inputStreamOrders = new ByteArrayInputStream(ordersInputStream.readAllBytes());

                return yandexDataCreator.getDataFromInputStream(inputStreamServices, inputStreamRealization, inputStreamOrders);
            } catch (IOException exception) {
                return new ArrayList<>();
            }

        } catch (IOException exception) {
            return new ArrayList<>();
        }
    }
}
