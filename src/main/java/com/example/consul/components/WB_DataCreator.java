package com.example.consul.components;

import com.example.consul.document.models.WB_TableRow;
import com.example.consul.dto.WB.WB_DetailReport;
import com.example.consul.mapping.WB_dataProcessing;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WB_DataCreator {
    private final WB_dataProcessing _process;

    public WB_DataCreator(WB_dataProcessing process) {
        _process = process;
    }

    public List<WB_TableRow> createTableRows(@NotNull List<WB_DetailReport> detailReport) {
        List<WB_TableRow> rows = new ArrayList<>();
        Map<String, Map<String, WB_TableRow>> rowMap = new HashMap<>();

        for (WB_DetailReport line : detailReport) {
            String saName = _process.checkOnDouble(line.getSaName());
            String country = _process.checkOnCountry(line.getSiteCountry());

            Map<String, WB_TableRow> existingRowMap = rowMap.computeIfAbsent(saName, k -> new HashMap<>());

            WB_TableRow existingRow = existingRowMap.get(country);
            if (existingRow == null) {
                WB_TableRow newRow = WB_TableRow.builder()
                        .article(saName)
                        .saleCount(_process.checkSaleCount(line))
                        .saleSum(_process.checkSaleSum(line))
                        .returnCount(_process.checkReturnCount(line))
                        .returnSum(_process.checkReturnSum(line))
                        .compensationLost(_process.checkCompensationLost(line))
                        .countLost(_process.checkCountLost(line))
                        .commission(_process.checkCommission(line))
                        .returnCommission(_process.checkReturnCommission(line))
                        .penalty(_process.checkPenalty(line))
                        .deduction(_process.checkDeduction(line))
                        .storageFee(_process.checkStorageFee(line))
                        .logistic(_process.checkLogistic(line))
                        .storno(_process.checkLogisticStorno(line))
                        .country(country)
                        .build();

                existingRowMap.put(country, newRow);
                rows.add(newRow);
            } else {
                existingRow.setSaleCount(existingRow.getSaleCount() + _process.checkSaleCount(line));
                existingRow.setSaleSum(existingRow.getSaleSum() + _process.checkSaleSum(line));
                existingRow.setReturnCount(existingRow.getReturnCount() + _process.checkReturnCount(line));
                existingRow.setReturnSum(existingRow.getReturnSum() + _process.checkReturnSum(line));
                existingRow.setCompensationLost(existingRow.getCompensationLost() + _process.checkCompensationLost(line));
                existingRow.setCountLost(existingRow.getCountLost() + _process.checkCountLost(line));
                existingRow.setCommission(existingRow.getCommission() + _process.checkCommission(line));
                existingRow.setReturnCommission(existingRow.getReturnCommission() + _process.checkReturnCommission(line));
                existingRow.setPenalty(existingRow.getPenalty() + _process.checkPenalty(line));
                existingRow.setDeduction(existingRow.getDeduction() + _process.checkDeduction(line));
                existingRow.setStorageFee(existingRow.getStorageFee() + _process.checkStorageFee(line));
                existingRow.setLogistic(existingRow.getLogistic() + _process.checkLogistic(line));
                existingRow.setStorno(existingRow.getStorno() + _process.checkLogisticStorno(line));
            }
        }

        return rows;
    }


    public Map<String, List<WB_TableRow>> createTableRows(@NotNull Map<String, List<WB_DetailReport>> detailReport) {
        return detailReport.entrySet().stream()
                .collect(Collectors.toConcurrentMap(
                        Map.Entry::getKey,
                        entry -> createTableRows(entry.getValue())
                ));
    }
}
