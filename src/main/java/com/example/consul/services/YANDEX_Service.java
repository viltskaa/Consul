package com.example.consul.services;

import com.example.consul.api.YANDEX_Api;
import com.example.consul.api.utils.YANDEX.YANDEX_PlacementType;
import com.example.consul.api.utils.YANDEX.YANDEX_ReportStatusType;
import com.example.consul.conditions.ConditionalWithDelayChecker;
import com.example.consul.document.models.YANDEX_TableRow;
import com.example.consul.dto.YANDEX.YANDEX_CreateReport;
import com.example.consul.dto.YANDEX.YANDEX_ReportInfo;
import com.example.consul.mapping.YANDEX_dataProcessing;
import org.antlr.v4.runtime.misc.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

@Service
public class YANDEX_Service {
    private final YANDEX_Api api;
    private final ConditionalWithDelayChecker reportChecker;

    public YANDEX_Service(YANDEX_Api api,
                          ConditionalWithDelayChecker reportChecker) {
        this.api = api;
        this.reportChecker = reportChecker;
    }

    public void setHeaders(@NotNull String auth) {
        api.setHeaders(auth);
    }

    public YANDEX_CreateReport getServicesReport(@NotNull Long businessId,
                                                 @NotNull String dateFrom,
                                                 @NotNull String dateTo,
                                                 @NotNull List<YANDEX_PlacementType> placementPrograms) {
        return api.createServicesReport(businessId, dateFrom, dateTo, placementPrograms);
    }

    public YANDEX_CreateReport getRealizationReport(@NotNull Long campaignId,
                                                    int year,
                                                    int month) {
        return api.createRealizationReport(campaignId, year, month);
    }

    public YANDEX_ReportInfo getReportInfo(@NotNull String reportId) {
        return api.getReportInfo(reportId);
    }

    public String scheduledGetServicesReport(@NotNull Long businessId,
                                             @NotNull String dateFrom,
                                             @NotNull String dateTo,
                                             @NotNull List<YANDEX_PlacementType> placementPrograms) {
        YANDEX_CreateReport report = getServicesReport(businessId, dateFrom, dateTo, placementPrograms);
        Boolean value = reportChecker.start(() -> {
            YANDEX_ReportInfo info = getReportInfo(report.getReportId());
            return info != null && info.getReportStatus().equals(YANDEX_ReportStatusType.DONE);
        }, 2L);

        if (value)
            return getReportInfo(report.getReportId()).getFileUrl();

        return null;
    }

    public String scheduledGetRealizationReport(@NotNull Long campaignId,
                                                int year,
                                                int month) {
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

    public List<YANDEX_TableRow> getDataForExcel(@NotNull Long campaignId,
                                                 int year,
                                                 int month,
                                                 @NotNull Long businessId,
                                                 @NotNull List<YANDEX_PlacementType> placementPrograms) throws IOException {
        Pair<String, String> date = getStartAndEndDateToDate(month, year);

        InputStream inputStreamRealization = new ByteArrayInputStream(new URL(scheduledGetRealizationReport(
                campaignId,
                year,
                month)
        ).openStream().readAllBytes());

        InputStream inputStreamServices = new ByteArrayInputStream(new URL(scheduledGetServicesReport(
                businessId,
                date.a,
                date.b,
                placementPrograms)
        ).openStream().readAllBytes());

        List<YANDEX_TableRow> data = YANDEX_dataProcessing.getTableRowList(inputStreamRealization, inputStreamServices);

        inputStreamRealization.close();
        inputStreamServices.close();

        return data;
    }
}
