package com.example.consul.mapping;

import com.example.consul.dto.WB_DetailReport;
import com.example.consul.dto.WB_DetailReportShort;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WB_dataProcessing {
    public static Map<String, List<WB_DetailReportShort>> groupBySaName(List<WB_DetailReport> wbDetailReports) {
        return wbDetailReports.stream()
                .filter(x -> x.getSa_name() != null)
                .map(WB_DetailReportShort::of)
                .collect(Collectors.groupingBy(WB_DetailReportShort::getSa_name));
    }
}
