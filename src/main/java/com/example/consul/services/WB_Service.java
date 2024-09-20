package com.example.consul.services;

import com.example.consul.api.WB_Api;
import com.example.consul.components.WB_DataCreator;
import com.example.consul.conditions.ConditionalWithDelayChecker;
import com.example.consul.document.v1.ExcelBuilderV1;
import com.example.consul.document.v1.configurations.ExcelConfig;
import com.example.consul.document.v1.configurations.HeaderConfig;
import com.example.consul.document.models.ReportFile;
import com.example.consul.document.models.WB_SaleRow;
import com.example.consul.document.models.WB_TableRow;
import com.example.consul.document.v2.ExcelBuilderV2;
import com.example.consul.document.v2.models.Sheet;
import com.example.consul.document.v2.models.Table;
import com.example.consul.dto.WB.WB_AdReport;
import com.example.consul.dto.WB.WB_DetailReport;
import com.example.consul.dto.WB.WB_SaleReport;
import com.example.consul.mapping.WB_dataProcessing;
import com.example.consul.utils.Clustering;
import com.example.consul.utils.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class WB_Service {
    private final WB_Api wbApi;
    private final WB_DataCreator wbDataCreator;
    private final ConditionalWithDelayChecker withDelayChecker;
    private final Clustering clustering;

    public WB_Service(
            WB_Api wbApi,
            WB_DataCreator wbDataCreator,
            ConditionalWithDelayChecker withDelayChecker,
            Clustering clustering
    ) {
        this.wbApi = wbApi;
        this.wbDataCreator = wbDataCreator;
        this.withDelayChecker = withDelayChecker;
        this.clustering = clustering;
    }

    public ReportFile createReport(
            @NotNull String apiKey,
            @NotNull Integer year,
            @NotNull Integer month
    ) {
        Map<String, List<WB_TableRow>> data = getData(apiKey, year, month);
        Map<String, Map<String, List<WB_TableRow>>> clusteredData = new TreeMap<>();

        for (Map.Entry<String, List<WB_TableRow>> entry : data.entrySet()) {
            Map<String, List<WB_TableRow>> clsData = clustering.of(entry.getValue(), "Не распределены");

            clsData.forEach((key, value) -> {
                if (clusteredData.containsKey(key)) {
                    Map<String, List<WB_TableRow>> keyValue = clusteredData.get(key);
                    if (keyValue.containsKey(entry.getKey())) {
                        keyValue.get(entry.getKey()).addAll(value);
                    } else {
                        keyValue.put(entry.getKey(), value);
                    }
                } else {
                    Map<String, List<WB_TableRow>> map = new TreeMap<>();
                    map.put(entry.getKey(), value);
                    clusteredData.put(key, map);
                }
            });
        }

        Comparator<WB_TableRow> wbTableRowComparator = Comparator.comparing(WB_TableRow::getArticle);

        return ExcelBuilderV2.<WB_TableRow>builder()
                .setFilename("report_wb.xlsx")
                .setSheets(
                        clusteredData.entrySet().stream()
                                .map(entry -> Sheet.<WB_TableRow>builder()
                                        .name(entry.getKey())
                                        .tables(
                                                entry.getValue().entrySet().stream()
                                                        .map(entryData ->
                                                                Table.<WB_TableRow>builder()
                                                                        .name(String.valueOf(entryData.getKey()))
                                                                        .data(entryData.getValue().stream()
                                                                                .sorted(wbTableRowComparator).toList())
                                                                        .build()
                                                        ).toList()
                                        ).build()).toList()
                )
                .build()
                .createDocument();
    }

    public ReportFile createReport(
            @NotNull String apiKey,
            @NotNull Integer year,
            @NotNull Integer month,
            @NotNull Integer weekNumber
    ) {
        List<WB_TableRow> data = getData(
                apiKey,
                year,
                month,
                weekNumber
        );

        return ExcelBuilderV1.createDocumentToReportFile(
                ExcelConfig.<WB_TableRow>builder()
                        .fileName("report_wb_" + month + "_" + year + ".xls")
                        .header(
                                HeaderConfig.builder()
                                        .title(apiKey)
                                        .description("%d-%d Неделя: %d".formatted(year, month, weekNumber))
                                        .build()
                        )
                        .data(List.of(data))
                        .sheetsName(List.of("1"))
                        .build()
        );
    }

    public ReportFile createReport(@NotNull String apiKey, @NotNull String day) {
        List<WB_SaleRow> data = getData(
                apiKey,
                day
        );

        return ExcelBuilderV1.createDocumentToReportFile(
                ExcelConfig.<WB_SaleRow>builder()
                        .fileName("report_wb_" + day + ".xls")
                        .header(
                                HeaderConfig.builder()
                                        .title(day)
                                        .description("Продажи")
                                        .build()
                        )
                        .data(List.of(data))
                        .sheetsName(List.of("1"))
                        .build()
        );
    }

    private boolean isNeedV1(@NotNull Integer month) {
        return month.equals(1);
    }

    public Map<String, List<WB_TableRow>> getData(
            @NotNull String apiKey,
            @NotNull Integer year,
            @NotNull Integer month
    ) {
        wbApi.setApiKey(apiKey);
        CompletableFuture<Map<String, List<WB_DetailReport>>> reportCompletableFuture = CompletableFuture
                .supplyAsync(() -> {
                    Map<String, List<WB_DetailReport>> weeksReport = new TreeMap<>();
                    DateUtils.WeekPeriod week4 = DateUtils.getNearMonday(year, month);

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

                    for (DateUtils.Week week: week4.getWeeks()) {
                        List<WB_DetailReport> weeklyReport = report.stream()
                                .filter(wbDetailReport -> week.isInRange(wbDetailReport.getDate_from())).toList();
                        weeksReport.put(week.toString(), weeklyReport);
                    }

                    return weeksReport;
                });

        return wbDataCreator.createTableRows(reportCompletableFuture.join());
    }

    public List<WB_TableRow> getData(
            @NotNull String apiKey,
            @NotNull Integer year,
            @NotNull Integer month,
            @NotNull Integer weekNumber
    ) {
        wbApi.setApiKey(apiKey);
        CompletableFuture<List<WB_DetailReport>> reportCompletableFuture = CompletableFuture
                .supplyAsync(() -> {
                    List<WB_DetailReport> report = getDetailReportByWeek(year, month, weekNumber);

                    if (report.size() == 100_000) {
                        withDelayChecker.start(() -> {
                            Long lastPageRrId = report.get(report.size() - 1).getRrd_id();
                            List<WB_DetailReport> nextPageReport = getDetailReportByWeekWithOffset(
                                    year,
                                    month,
                                    weekNumber,
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

    // @todo move to mapping
    private List<WB_SaleRow> mapToSaleRows(Map<String, Long> salesCountMap) {
        return salesCountMap.entrySet().stream()
                .map(entry -> WB_SaleRow.builder()
                        .article(entry.getKey())
                        .count(Math.toIntExact(entry.getValue()))
                        .build()
                )
                .toList();
    }

    public List<WB_SaleRow> getData(@NotNull String apiKey, @NotNull String day) {
        wbApi.setApiKey(apiKey);
        return mapToSaleRows(WB_dataProcessing.getSalesCount(getSaleReport(day)));
    }

    public List<WB_DetailReport> getDetailReportByYearAndMonth(@NotNull Integer year, @NotNull Integer month) {
        DateUtils.WeekPeriod date = DateUtils.getNearMonday(year, month);
        return isNeedV1(month)
                ? getDetailReportV1(date.getFirstDay(), date.getLastDay())
                : getDetailReportV5(date.getFirstDay(), date.getLastDay());
    }

    public List<WB_DetailReport> getDetailReportByWeek(
            @NotNull Integer year,
            @NotNull Integer month,
            @NotNull Integer weekNumber
    ) {
        DateUtils.Week week = DateUtils.getWeek(year, month, weekNumber);
        return isNeedV1(month)
                ? getDetailReportV1(week.getFirstDay(), week.getLastDay())
                : getDetailReportV5(week.getFirstDay(), week.getLastDay());
    }

    public List<WB_DetailReport> getDetailReportByYearAndMonthWithOffset(
            @NotNull Integer year,
            @NotNull Integer month,
            @NotNull Long rrdId
    ) {
        DateUtils.Month date = DateUtils.getMonth(year, month);
        return isNeedV1(month)
                ? getDetailReportV1(date.getFirstDay(), date.getLastDay(), rrdId)
                : getDetailReportV5(date.getFirstDay(), date.getLastDay(), rrdId);
    }

    public List<WB_DetailReport> getDetailReportByWeekWithOffset(
            @NotNull Integer year,
            @NotNull Integer month,
            @NotNull Integer weekNumber,
            @NotNull Long rrdId
    ) {
        DateUtils.Week week = DateUtils.getWeek(year, month, weekNumber);
        return isNeedV1(month)
                ? getDetailReportV1(week.getFirstDay(), week.getLastDay(), rrdId)
                : getDetailReportV5(week.getFirstDay(), week.getLastDay(), rrdId);
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

    public List<WB_DetailReport> getDetailReportV1(
            @NotNull String dateFrom,
            @NotNull String dateTo,
            @NotNull Long rrdId
    ) {
        try {
            return wbApi.getDetailReportWithOffsetV1(dateFrom, dateTo, rrdId);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    public List<WB_DetailReport> getDetailReportV5(
            @NotNull String dateFrom,
            @NotNull String dateTo,
            @NotNull Long rrdId
    ) {
        try {
            return wbApi.getDetailReportWithOffsetV5(dateFrom, dateTo, rrdId);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    public List<WB_SaleReport> getSaleReport(@NotNull String dateFrom) {
        try {
            return wbApi.getSaleReport(dateFrom);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    @Deprecated
    public List<WB_AdReport> getAdReport(@NotNull String dateFrom, @NotNull String dateTo) {
        try {
            return wbApi.getAdReport(dateFrom, dateTo);
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }
}
