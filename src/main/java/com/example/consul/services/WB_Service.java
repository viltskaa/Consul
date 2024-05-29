package com.example.consul.services;

import com.example.consul.api.WB_Api;
import com.example.consul.components.WB_DataCreator;
import com.example.consul.conditions.ConditionalWithDelayChecker;
import com.example.consul.document.ExcelBuilder;
import com.example.consul.document.configurations.ExcelConfig;
import com.example.consul.document.configurations.HeaderConfig;
import com.example.consul.document.models.WB_TableRow;
import com.example.consul.dto.WB.WB_AdReport;
import com.example.consul.dto.WB.WB_DetailReport;
import com.example.consul.utils.Clustering;
import org.antlr.v4.runtime.misc.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class WB_Service {
    private final WB_Api wbApi;
    private final WB_DataCreator wbDataCreator;
    private final ConditionalWithDelayChecker withDelayChecker;
    private final Clustering clustering;

    public WB_Service(WB_Api wbApi, WB_DataCreator wbDataCreator, ConditionalWithDelayChecker withDelayChecker, Clustering clustering) {
        this.wbApi = wbApi;
        this.wbDataCreator = wbDataCreator;
        this.withDelayChecker = withDelayChecker;
        this.clustering = clustering;
    }

    public byte[] createReport(@NotNull String apiKey,
                               @NotNull Integer year,
                               @NotNull Integer month) {
        List<WB_TableRow> data = getData(
                apiKey,
                year,
                month
        );

        Map<String, List<WB_TableRow>> clusteredData = clustering.of(data);

        return ExcelBuilder.createDocumentToByteArray(
                ExcelConfig.<WB_TableRow>builder()
                        .fileName("report_wb_" + month + "_" + year + ".xls")
                        .header(
                                HeaderConfig.builder()
                                        .title("WB")
                                        .description("NEW METHOD")
                                        .build()
                        )
                        .data(clusteredData.values().stream().toList())
                        .sheetsName(clusteredData.keySet().stream().toList())
                        .build()
        );
    }

    private Pair<String, String> getDateFrom(@NotNull Integer year, @NotNull Integer month) {
        LocalDate date = LocalDate.of(year, month, 1);

        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        String startOfMonthString = startOfMonth.atStartOfDay(ZoneOffset.UTC)
                .toString().replace("T00:00", "T00:00:00.000");
        String endOfMonthString = endOfMonth.atStartOfDay(ZoneOffset.UTC)
                .plusDays(1).minusNanos(1000000).toString();

        return new Pair<>(startOfMonthString, endOfMonthString);
    }

    private boolean isNeedV1(@NotNull Integer month) {
        return month.equals(1);
    }

    public List<WB_TableRow> getData(@NotNull String apiKey,
                                     @NotNull Integer year,
                                     @NotNull Integer month) {
        wbApi.setApiKey(apiKey);
        CompletableFuture<List<WB_DetailReport>> reportCompletableFuture = CompletableFuture
                .supplyAsync(() -> {
                    List<WB_DetailReport> report = getDetailReportByYearAndMonth(year, month);
                    if (report == null) {
                        return null;
                    }

                    if (report.size() == 100_000) {
                        withDelayChecker.start(() -> {
                            Long lastPageRrId = report.get(report.size() - 1).getRrd_id();
                            List<WB_DetailReport> nextPageReport = getDetailReportByYearAndMonthWithOffset(
                                    year,
                                    month,
                                    lastPageRrId
                            );
                            if (nextPageReport == null) return true;
                            report.addAll(nextPageReport);
                            return false;
                        }, 65L);
                    }

                    return report;
                });

        return wbDataCreator.createTableRows(reportCompletableFuture.join());
    }

    public List<WB_DetailReport> getDetailReportByYearAndMonth(@NotNull Integer year,
                                                               @NotNull Integer month) {
        Pair<String, String> dates = getDateFrom(year, month);
        return isNeedV1(month)
                ? getDetailReportV1(dates.a, dates.b)
                : getDetailReportV5(dates.a, dates.b);
    }

    public List<WB_DetailReport> getDetailReportByYearAndMonthWithOffset(@NotNull Integer year,
                                                                         @NotNull Integer month,
                                                                         @NotNull Long rrdId) {
        Pair<String, String> dates = getDateFrom(year, month);
        return isNeedV1(month)
                ? getDetailReportV1(dates.a, dates.b, rrdId)
                : getDetailReportV5(dates.a, dates.b, rrdId);
    }

    public List<WB_DetailReport> getDetailReportV1(@NotNull String dateFrom, @NotNull String dateTo) {
        try {
            return wbApi.getDetailReportV1(dateFrom, dateTo);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    public List<WB_DetailReport> getDetailReportV5(@NotNull String dateFrom, @NotNull String dateTo) {
        try {
            return wbApi.getDetailReportV5(dateFrom, dateTo);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    public List<WB_DetailReport> getDetailReportV1(@NotNull String dateFrom, @NotNull String dateTo, @NotNull Long rrdId) {
        try {
            return wbApi.getDetailReportWithOffsetV1(dateFrom, dateTo, rrdId);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    public List<WB_DetailReport> getDetailReportV5(@NotNull String dateFrom, @NotNull String dateTo, @NotNull Long rrdId) {
        try {
            return wbApi.getDetailReportWithOffsetV5(dateFrom, dateTo, rrdId);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    public List<WB_AdReport> getAdReport(@NotNull String dateFrom, @NotNull String dateTo) {
        try {
            return wbApi.getAdReport(dateFrom, dateTo);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }
}
