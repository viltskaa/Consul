package com.example.consul.services;

import com.example.consul.api.YANDEX_Api;
import com.example.consul.api.utils.YANDEX.YANDEX_PlacementType;
import com.example.consul.api.utils.YANDEX.YANDEX_ReportStatusType;
import com.example.consul.conditions.ConditionalWithDelayChecker;
import com.example.consul.dto.YANDEX.YANDEX_CreateReport;
import com.example.consul.dto.YANDEX.YANDEX_ReportInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YANDEX_Service {
    private final YANDEX_Api api;
    private final ConditionalWithDelayChecker reportChecker;

    public YANDEX_Service(YANDEX_Api api,
                          ConditionalWithDelayChecker reportChecker){
        this.api = api;
        this.reportChecker = reportChecker;
    }

    public void setHeaders(@NotNull String auth) {
        api.setHeaders(auth);
    }

    public YANDEX_CreateReport getServicesReport(@NotNull Long businessId,
                                                 @NotNull String dateFrom,
                                                 @NotNull String dateTo,
                                                 @NotNull List<YANDEX_PlacementType> placementPrograms){
        return api.createServicesReport(businessId, dateFrom, dateTo, placementPrograms);
    }

    public YANDEX_CreateReport getRealizationReport(@NotNull Long campaignId,
                                                    int year,
                                                    int month){
        return api.createRealizationReport(campaignId, year, month);
    }

    public YANDEX_ReportInfo getReportInfo(@NotNull String reportId){
        return api.getReportInfo(reportId);
    }

    public String scheduledGetServicesReport(@NotNull Long businessId,
                                             @NotNull String dateFrom,
                                             @NotNull String dateTo,
                                             @NotNull List<YANDEX_PlacementType> placementPrograms){
        YANDEX_CreateReport report = getServicesReport(businessId, dateFrom, dateTo, placementPrograms);
        Boolean value = reportChecker.start(() -> {
            YANDEX_ReportInfo info = getReportInfo(report.getReportId());
            return info != null && info.getReportStatus().equals(YANDEX_ReportStatusType.DONE);
        }, 2L);

        if(value)
            return getReportInfo(report.getReportId()).getFileUrl();

        return null;
    }

    public String scheduledGetRealizationReport(@NotNull Long campaignId,
                                                int year,
                                                int month){
        YANDEX_CreateReport report = getRealizationReport(campaignId, year,month);
        Boolean value = checkReport(report.getReportId(), report.getCreationTime());

        if(value)
            return getReportInfo(report.getReportId()).getFileUrl();

        return null;
    }

    private Boolean checkReport(@NotNull String reportId,
                                @NotNull Long creationTime){
        return reportChecker.start(() -> {
            YANDEX_ReportInfo info = getReportInfo(reportId);
            return info != null && info.getReportStatus().equals(YANDEX_ReportStatusType.DONE);
        }, creationTime);
    }
}
