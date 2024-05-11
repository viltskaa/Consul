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
        Map<String, Double> logisticSum = WB_dataProcessing.sumLogistic(groupedDetail);

        Map<String, List<Object>> mergedMap = new HashMap<>(deliveryAmount.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.asList(
                        entry.getValue(),
                        retailSum.getOrDefault(entry.getKey(), 0.0),
                        returnAmount.getOrDefault(entry.getKey(), 0),
                        returnSum.getOrDefault(entry.getKey(), 0.0),
                        saleCommission.getOrDefault(entry.getKey(), 0.0),
                        returnCommission.getOrDefault(entry.getKey(), 0.0),
                        logisticSum.getOrDefault(entry.getKey(), 0.0)
                ))));

        return mergedMap.entrySet().stream().map(x -> {
           List<Object> values = x.getValue();
           return WB_TableRow.builder()
                   .saName(x.getKey())
                   .saleAmount((Integer) values.get(0))
                   .sumSale((Double) values.get(1))
                   .returnAmount((Integer) values.get(2))
                   .sumReturn((Double) values.get(3))
                   .stornoReturn(0.0)
                   .sumStornoReturn(0.0)
                   .stornoSale(0.0)
                   .stornoSumSale(0.0)
                   .amountCompensationForLost(0.0)
                   .allSumCompensationForLost(0.0)
                   .partSumCompensationForLost(0.0)
                   .compensation((Double) values.get(4))
                   .acquiringSale((Double) values.get(5))
                   .acquiringReturn(0.0)
                   .acquiringPvzReturn(0.0)
                   .additional(0.0)
                   .penalty(0.0)
                   .deduction(0.0)
                   .storage(0.0)
                   .logistic((Double) values.get(6))
                   .stornoLogistic(0.0)
                   .build();
        }).collect(Collectors.toList());
    }
}
