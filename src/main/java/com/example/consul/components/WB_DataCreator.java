package com.example.consul.components;

import com.example.consul.document.models.WB_TableRow;
import com.example.consul.dto.WB.WB_DetailReport;
import com.example.consul.dto.WB.WB_OperationName;
import com.example.consul.mapping.WB_dataProcessing;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WB_DataCreator {
    public List<WB_TableRow> createTableRows(@NotNull List<WB_DetailReport> detailReport) {
        Map<String, List<WB_DetailReport>> groupedDetail = WB_dataProcessing
                .groupBySaName(detailReport);

        Map<String, Integer> deliveryAmount = WB_dataProcessing.sumDeliveryAmount(groupedDetail);
        Map<String, Integer> returnAmount = WB_dataProcessing.sumReturnAmount(groupedDetail);
        Map<String, Double> retailSum = WB_dataProcessing.sumRetail(groupedDetail);
        Map<String, Double> returnSum = WB_dataProcessing.sumReturn(groupedDetail);
        Map<String, Double> saleCommission = WB_dataProcessing.sumCommission(
                groupedDetail,
                WB_OperationName.SALE
        );
        Map<String, Double> returnCommission = WB_dataProcessing.sumCommission(
                groupedDetail,
                WB_OperationName.RETURN
        );
        Map<String, Double> refundCommission = WB_dataProcessing.sumRefundCommission(groupedDetail);
        Map<String, Double> attorney = WB_dataProcessing.sumAttorney(
                groupedDetail,
                WB_OperationName.SALE
        );
        Map<String, Double> sumRebill = WB_dataProcessing.sumRebill(groupedDetail);

        Map<String, Double> logisticSum = WB_dataProcessing.sumLogistic(groupedDetail);
        Map<String, Double> penaltySum = WB_dataProcessing.sumPenalty(groupedDetail);

        Map<String, Double> compensationLost = WB_dataProcessing.sumCompensationLosted(groupedDetail);
        Map<String, Double> compensationReplace = WB_dataProcessing.sumCompensationReplaced(groupedDetail);
        Map<String, Double> compensationDefect = WB_dataProcessing.sumCompensationDefected(groupedDetail);

        Double retaliatedProduct = deliveryAmount.values().stream().mapToDouble(x -> x).sum()
                - returnAmount.values().stream().mapToDouble(x -> x).sum();

        Double deduction = WB_dataProcessing.sumDoubleValuesByConditions(
                detailReport,
                x -> x.getSupplier_oper_name().equals(WB_OperationName.DEDUCTION.toString()),
                WB_DetailReport::getDeduction
        ) / retaliatedProduct;

        Double storage = WB_dataProcessing.sumDoubleValuesByConditions(
                detailReport,
                x -> x.getSupplier_oper_name().equals(WB_OperationName.STORAGE.toString())
                        || x.getSupplier_oper_name().equals(WB_OperationName.STORAGE_REFUND.toString()),
                WB_DetailReport::getStorage_fee
        ) / retaliatedProduct;

        Map<String, List<Object>> mergedMap = new HashMap<>(deliveryAmount.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.asList(
                        entry.getValue(),
                        retailSum.getOrDefault(entry.getKey(), 0.0), // 1
                        returnAmount.getOrDefault(entry.getKey(), 0), // 2
                        returnSum.getOrDefault(entry.getKey(), 0.0), // 3
                        saleCommission.getOrDefault(entry.getKey(), 0.0), // 4
                        returnCommission.getOrDefault(entry.getKey(), 0.0), // 5
                        logisticSum.getOrDefault(entry.getKey(), 0.0), // 6
                        sumRebill.getOrDefault(entry.getKey(), 0.0), // 7
                        penaltySum.getOrDefault(entry.getKey(), 0.0), // 8
                        attorney.getOrDefault(entry.getKey(), 0.0), // 9
                        refundCommission.getOrDefault(entry.getKey(), 0.0), // 10
                        deduction * entry.getValue(), // 11
                        storage * entry.getValue(), // 12
                        compensationLost.getOrDefault(entry.getKey(), 0.0), // 13
                        compensationReplace.getOrDefault(entry.getKey(), 0.0), // 14
                        compensationDefect.getOrDefault(entry.getKey(), 0.0) // 15
                ))));

        return mergedMap.entrySet().stream().map(x -> {
           List<Object> values = x.getValue();
           return WB_TableRow.builder()
                   .article(x.getKey())
                   .retailAmount((Integer) values.get(0))
                   .retailSum((Double) values.get(1))
                   .returnAmount((Integer) values.get(2))
                   .sumReturn((Double) values.get(3))
                   .sumCompensationForLost((Double) values.get(13))
                   .sumCompensationForReplace((Double) values.get(14))
                   .sumCompensationForDefected((Double) values.get(15))
                   .acquiringSale((Double) values.get(9))
                   .acquiringReturn((Double) values.get(10))
                   .additional(0.0)
                   .penalty((Double) values.get(7))
                   .deduction((Double) values.get(11))
                   .storage((Double) values.get(12))
                   .logistic((Double) values.get(6))
                   .build();
        }).collect(Collectors.toList());
    }
}
