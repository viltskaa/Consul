package com.example.consul.components;

import com.example.consul.document.models.YANDEX_TableRow;
import com.example.consul.mapping.YANDEX_dataProcessing;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.example.consul.mapping.excelProcessing.FormatExcel.ungroupCells;

@Component
public class YANDEX_DataCreator {
    public List<YANDEX_TableRow> getDataFromInputStream(InputStream inputStreamService, InputStream inputStreamRealization, InputStream inputStreamOrders) throws IOException {
        Workbook wbService = WorkbookFactory.create(inputStreamService);

        final Sheet[] sheetService = {
                wbService.getSheetAt(1),
                wbService.getSheetAt(3),
                wbService.getSheetAt(5),
                wbService.getSheetAt(9),
                wbService.getSheetAt(12),
                wbService.getSheetAt(19),
                wbService.getSheetAt(13),
                wbService.getSheetAt(22),
                wbService.getSheetAt(7)
        };

        Workbook wbRealization = WorkbookFactory.create(inputStreamRealization);

        final Sheet[] sheetRealization = {
                wbRealization.getSheetAt(2),
                wbRealization.getSheetAt(4),
                wbRealization.getSheetAt(1)
        };

        Workbook wbOrders = WorkbookFactory.create(inputStreamOrders);

        final Sheet[] sheetOrders = {
                wbOrders.getSheetAt(1)
        };

        ungroupCells(sheetOrders[0]);

        CompletableFuture<Map<String, Double>> placingOnShowcaseCompletableFuture = CompletableFuture
                .supplyAsync(() -> {
                    ungroupCells(sheetService[0]);
                    return YANDEX_dataProcessing.getMapPlacingOnShowcase(sheetService[0]);
                });

        CompletableFuture<Map<String, Double>> loyaltyProgramCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.getMapLoyaltyProgram(sheetService[1]));

        CompletableFuture<Map<String, Double>> boostSalesCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.getMapBoostSales(sheetService[2]));

        CompletableFuture<Map<String, Double>> deliveryToConsumerCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.getMapDeliveryToConsumer(sheetService[3]));

        CompletableFuture<Map<String, Double>> acceptAndTransferPaymentCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.getMapAcceptAndTransferPayment(
                        sheetService[4],
                        sheetService[6]
                ));

        CompletableFuture<Map<String, Double>> shelvesCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.calculateServiceCostRatio(
                        sheetRealization[0],
                        sheetRealization[1],
                        sheetService[8]
                ));

        CompletableFuture<Map<String, Double>> favorSortingCenterPaymentCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.getMapSortingCenter(
                        sheetService[3],
                        sheetRealization[2],
                        sheetService[5],
                        sheetService[4],
                        sheetOrders[0]));

        CompletableFuture<Map<String, Double>> storageReturnCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.getMapStorageReturn(
                        sheetService[3],
                        sheetRealization[2],
                        sheetService[7],
                        sheetService[4],
                        sheetOrders[0]
                ));

        CompletableFuture<Map<String, Double>> deliveredCostCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.getMapDeliveryCost(sheetRealization[0]));

        CompletableFuture<Map<String, Integer>> deliveredCountCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.getMapDeliveryCount(sheetRealization[0]));

        CompletableFuture<Map<String, Integer>> returnCountCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.getMapReturnCount(sheetRealization[1]));

        CompletableFuture<Map<String, Double>> returnCostCompletableFuture = CompletableFuture
                .supplyAsync(() -> YANDEX_dataProcessing.getMapReturnCost(sheetRealization[1]));

        Map<String, Double> placingOnShowcase = placingOnShowcaseCompletableFuture.join();
        Map<String, Double> loyaltyProgram = loyaltyProgramCompletableFuture.join();
        Map<String, Double> boostSales = boostSalesCompletableFuture.join();
        Map<String, Double> deliveryToConsumer = deliveryToConsumerCompletableFuture.join();
        Map<String, Double> acceptAndTransferPayment = acceptAndTransferPaymentCompletableFuture.join();
        Map<String, Double> favorSortingCenterPayment = favorSortingCenterPaymentCompletableFuture.join();
        Map<String, Double> deliveredCost = deliveredCostCompletableFuture.join();
        Map<String, Integer> deliveredCount = deliveredCountCompletableFuture.join();
        Map<String, Integer> returnCount = returnCountCompletableFuture.join();
        Map<String, Double> returnCost = returnCostCompletableFuture.join();
        Map<String, Double> storageReturn = storageReturnCompletableFuture.join();
        Map<String, Double> shelves = shelvesCompletableFuture.join();

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(acceptAndTransferPayment.keySet());
        allKeys.addAll(deliveredCount.keySet());
        allKeys.addAll(deliveredCost.keySet());
        allKeys.addAll(returnCount.keySet());
        allKeys.addAll(returnCost.keySet());
        allKeys.addAll(placingOnShowcase.keySet());
        allKeys.addAll(deliveryToConsumer.keySet());
        allKeys.addAll(favorSortingCenterPayment.keySet());
        allKeys.addAll(storageReturn.keySet());
        allKeys.addAll(loyaltyProgram.keySet());
        allKeys.addAll(boostSales.keySet());
        allKeys.addAll(shelves.keySet());

        Map<String, List<Object>> mergedMap = new HashMap<>();

        for (String key : allKeys) {
            mergedMap.put(key, Arrays.asList(
                    deliveredCount.getOrDefault(key, 0).doubleValue(),
                    deliveredCost.getOrDefault(key, 0.0),
                    returnCount.getOrDefault(key, 0).doubleValue(),
                    returnCost.getOrDefault(key, 0.0),
                    placingOnShowcase.getOrDefault(key, 0.0),
                    deliveryToConsumer.getOrDefault(key, 0.0),
                    acceptAndTransferPayment.getOrDefault(key, 0.0),
                    favorSortingCenterPayment.getOrDefault(key, 0.0),
                    storageReturn.getOrDefault(key, 0.0),
                    0.0, // Placeholder for "Расходы на рекламные кампании"
                    loyaltyProgram.getOrDefault(key, 0.0),
                    boostSales.getOrDefault(key, 0.0),
                    shelves.getOrDefault(key, 0.0),
                    0.0 // Placeholder for "Услуги продвижения"
            ));
        }

        return mergedMap.entrySet().stream().map(entry -> {
            List<Object> values = entry.getValue();
            return new YANDEX_TableRow(
                    entry.getKey(),
                    (Double) values.get(0), // deliveryCount
                    (Double) values.get(1), // accrued
                    (Double) values.get(2), // returnCount
                    (Double) values.get(3), // returnCost
                    (Double) values.get(4), // showcasePlacing
                    (Double) values.get(5), // deliveryToConsumer
                    (Double) values.get(6), // acceptAndTransferPayment
                    (Double) values.get(7), // favorSorting
                    (Double) values.get(8), // unredeemedStorage
                    (Double) values.get(9), // adCampaignCost
                    (Double) values.get(10), // loyaltyProgram
                    (Double) values.get(11), // boostSales
                    (Double) values.get(12), //shelves
                    (Double) values.get(13)  // promotionFavor
            );
        }).toList();
    }
}
