package com.example.consul.mapping;

import com.example.consul.document.models.YANDEX_TableRow;
import com.example.consul.mapping.sheets.*;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.consul.mapping.excelProcessing.DataFromExcel.*;
import static com.example.consul.mapping.excelProcessing.FormatExcel.ungroupCells;

public class YANDEX_dataProcessing {
    public static List<YANDEX_TableRow> getDataFromInputStream(InputStream inputStreamService, InputStream inputStreamRealization, InputStream inputStreamOrders) throws IOException {
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
                    return getMapPlacingOnShowcase(sheetService[0]);
                });

        CompletableFuture<Map<String, Double>> loyaltyProgramCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapLoyaltyProgram(sheetService[1]));

        CompletableFuture<Map<String, Double>> boostSalesCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapBoostSales(sheetService[2]));

        CompletableFuture<Map<String, Double>> deliveryToConsumerCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapDeliveryToConsumer(sheetService[3]));

        CompletableFuture<Map<String, Double>> acceptAndTransferPaymentCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapAcceptAndTransferPayment(
                        sheetService[4],
                        sheetService[6]
                ));

        CompletableFuture<Map<String, Double>> shelvesCompletableFuture = CompletableFuture
                .supplyAsync(() -> calculateServiceCostRatio(
                        sheetRealization[0],
                        sheetRealization[1],
                        sheetService[8]
                ));

        CompletableFuture<Map<String, Double>> favorSortingCenterPaymentCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapSortingCenter(
                    sheetService[3],
                    sheetRealization[2],
                    sheetService[5],
                    sheetService[4],
                    sheetOrders[0]));

        CompletableFuture<Map<String, Double>> storageReturnCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapStorageReturn(
                        sheetService[3],
                        sheetRealization[2],
                        sheetService[7],
                        sheetService[4],
                        sheetOrders[0]
                ));

        CompletableFuture<Map<String, Double>> deliveredCostCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapDeliveryCost(sheetRealization[0]));

        CompletableFuture<Map<String, Integer>> deliveredCountCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapDeliveryCount(sheetRealization[0]));

        CompletableFuture<Map<String, Integer>> returnCountCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapReturnCount(sheetRealization[1]));

        CompletableFuture<Map<String, Double>> returnCostCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapReturnCost(sheetRealization[1]));

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

    public static Map<String, Double> getMapSortingCenter(Sheet sheetDelivery, Sheet sheetShip, Sheet sheetSortingCenter, Sheet sheetAcceptPay, Sheet sheetTransact) {
        CompletableFuture<List<YANDEX_AcceptingPayment>> acceptFuture = CompletableFuture.supplyAsync(() -> getAcceptingPayment(sheetAcceptPay));
        CompletableFuture<List<YANDEX_DeliveryCustomer>> deliveryFuture = CompletableFuture.supplyAsync(() -> getDeliveryCustomer(sheetDelivery));
        CompletableFuture<List<YANDEX_GoodsInDelivery>> goodsFuture = CompletableFuture.supplyAsync(() -> getGoodsInDelivery(sheetShip));
        CompletableFuture<List<YANDEX_TransactionsOrdersAndProducts>> transactFuture = CompletableFuture.supplyAsync(() -> getTransactionsOnOrdersAndProducts(sheetTransact));
        CompletableFuture<List<YANDEX_ProcessingOrders>> sortingFuture = CompletableFuture.supplyAsync(() -> getProcessingOrders(sheetSortingCenter));

        try {
            List<YANDEX_AcceptingPayment> listAccept = acceptFuture.get();
            List<YANDEX_DeliveryCustomer> listDel = deliveryFuture.get();
            List<YANDEX_GoodsInDelivery> listDelivery = goodsFuture.get();
            List<YANDEX_TransactionsOrdersAndProducts> listTransact = transactFuture.get();
            List<YANDEX_ProcessingOrders> listSorting = sortingFuture.get();

            Map<Long, Set<String>> orderNumberToSkuSetMap = Stream.of(listDel, listAccept, listDelivery, listTransact)
                    .flatMap(List::stream)
                    .parallel()
                    .collect(Collectors.toConcurrentMap(
                            YANDEX_dataProcessing::getOrderNumberFromRecord,
                            record -> new HashSet<>(Collections.singletonList(getSkuFromRecord(record))),
                            (existing, replacement) -> {
                                existing.addAll(replacement);
                                return existing;
                            },
                            ConcurrentHashMap::new
                    ));

            Map<Long, Double> orderNumberToTotalTariffMap = listSorting.parallelStream()
                    .collect(Collectors.groupingBy(
                            YANDEX_ProcessingOrders::getOrderNumber,
                            Collectors.summingDouble(YANDEX_ProcessingOrders::getTariff)
                    ));

            Map<String, Double> skuToFinalTariffMap = new ConcurrentHashMap<>();

            orderNumberToSkuSetMap.forEach((orderNumber, skuSet) -> {
                Double totalTariff = orderNumberToTotalTariffMap.getOrDefault(orderNumber, 0.0);
                if (totalTariff > 0 && !skuSet.isEmpty()) {
                    double dividedTariff = totalTariff / skuSet.size();
                    skuSet.forEach(sku -> skuToFinalTariffMap.merge(sku, dividedTariff, Double::sum));
                }
            });

            skuToFinalTariffMap.replaceAll((sku, value) -> Math.round(value * 100.0) / 100.0);

            return skuToFinalTariffMap;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while processing data", e);
        }
    }

    private static Long getOrderNumberFromRecord(Object record) {
        if (record instanceof YANDEX_DeliveryCustomer) {
            return ((YANDEX_DeliveryCustomer) record).getOrderNumber();
        } else if (record instanceof YANDEX_AcceptingPayment) {
            return ((YANDEX_AcceptingPayment) record).getOrderNumber();
        } else if (record instanceof YANDEX_GoodsInDelivery) {
            return ((YANDEX_GoodsInDelivery) record).getOrderNumber();
        } else if (record instanceof YANDEX_TransactionsOrdersAndProducts) {
            return ((YANDEX_TransactionsOrdersAndProducts) record).getOrderNumber();
        }
        throw new IllegalArgumentException("Unknown record type");
    }

    private static String getSkuFromRecord(Object record) {
        if (record instanceof YANDEX_DeliveryCustomer) {
            return ((YANDEX_DeliveryCustomer) record).getSku();
        } else if (record instanceof YANDEX_AcceptingPayment) {
            return ((YANDEX_AcceptingPayment) record).getSku();
        } else if (record instanceof YANDEX_GoodsInDelivery) {
            return ((YANDEX_GoodsInDelivery) record).getProductSku();
        } else if (record instanceof YANDEX_TransactionsOrdersAndProducts) {
            return ((YANDEX_TransactionsOrdersAndProducts) record).getSku();
        }
        throw new IllegalArgumentException("Unknown record type");
    }

    public static Map<String, Double> getMapStorageReturn(Sheet sheetDelivery, Sheet sheetShip, Sheet sheetStorageReturn, Sheet sheetAcceptPay, Sheet sheetTransact) {
        CompletableFuture<List<YANDEX_AcceptingPayment>> acceptFuture = CompletableFuture.supplyAsync(() -> getAcceptingPayment(sheetAcceptPay));
        CompletableFuture<List<YANDEX_DeliveryCustomer>> deliveryFuture = CompletableFuture.supplyAsync(() -> getDeliveryCustomer(sheetDelivery));
        CompletableFuture<List<YANDEX_StorageReturns>> storageReturnFuture = CompletableFuture.supplyAsync(() -> getStorageReturns(sheetStorageReturn));
        CompletableFuture<List<YANDEX_GoodsInDelivery>> goodsFuture = CompletableFuture.supplyAsync(() -> getGoodsInDelivery(sheetShip));
        CompletableFuture<List<YANDEX_TransactionsOrdersAndProducts>> transactFuture = CompletableFuture.supplyAsync(() -> getTransactionsOnOrdersAndProducts(sheetTransact));

        try {
            List<YANDEX_AcceptingPayment> listAccept = acceptFuture.get();
            List<YANDEX_DeliveryCustomer> listDel = deliveryFuture.get();
            List<YANDEX_StorageReturns> listSorting = storageReturnFuture.get();
            List<YANDEX_GoodsInDelivery> listDelivery = goodsFuture.get();
            List<YANDEX_TransactionsOrdersAndProducts> listTransact = transactFuture.get();

            Map<Long, List<String>> orderNumberToSkuListMap = Stream.of(listDel, listAccept, listDelivery, listTransact)
                    .flatMap(List::stream)
                    .parallel()
                    .collect(Collectors.groupingBy(
                            YANDEX_dataProcessing::getOrderNumberFromRecord,
                            ConcurrentHashMap::new,
                            Collectors.mapping(YANDEX_dataProcessing::getSkuFromRecord, Collectors.toList())
                    ));

            Map<Long, Double> orderNumberToTotalTariffMap = listSorting.parallelStream()
                    .collect(Collectors.groupingBy(
                            YANDEX_StorageReturns::getOrderNumber,
                            Collectors.summingDouble(YANDEX_StorageReturns::getServiceCost)
                    ));

            Map<String, Double> skuToFinalTariffMap = new ConcurrentHashMap<>();

            orderNumberToSkuListMap.forEach((orderNumber, skuList) -> {
                Double totalTariff = orderNumberToTotalTariffMap.getOrDefault(orderNumber, 0.0);
                if (totalTariff > 0 && !skuList.isEmpty()) {
                    double dividedTariff = totalTariff / skuList.size();
                    skuList.forEach(sku -> skuToFinalTariffMap.merge(sku, dividedTariff, Double::sum));
                }
            });

            skuToFinalTariffMap.replaceAll((sku, value) -> Math.round(value * 100.0) / 100.0);

            return skuToFinalTariffMap;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while processing data", e);
        }
    }

    public static Map<String, Double> getMapDeliveryCost(Sheet sheet) {
        List<YANDEX_DeliveredGoods> list = getDeliveredGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveredGoods::getProductSku,
                        Collectors.summingDouble(YANDEX_DeliveredGoods::getTotalPriceWithDiscount)));
    }

    @Deprecated
    public static Map<String, Double> getMapMarketplaceDiscount(Sheet sheet) {
        List<YANDEX_DeliveredGoods> list = getDeliveredGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveredGoods::getProductSku,
                        Collectors.summingDouble(YANDEX_DeliveredGoods::getTotalDiscount)));
    }

    public static Map<String, Integer> getMapDeliveryCount(Sheet sheet) {
        List<YANDEX_DeliveredGoods> list = getDeliveredGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveredGoods::getProductSku,
                        Collectors.summingInt(YANDEX_DeliveredGoods::getQuantityDelivered)));
    }

    public static Map<String, Double> getMapReturnCost(Sheet sheet) {
        List<YANDEX_ReturnedGoods> list = getReturnedGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_ReturnedGoods::getProductSku,
                        Collectors.summingDouble(YANDEX_ReturnedGoods::getPriceWithDiscount)));
    }

    public static Map<String, Integer> getMapReturnCount(Sheet sheet) {
        List<YANDEX_ReturnedGoods> list = getReturnedGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_ReturnedGoods::getProductSku,
                        Collectors.summingInt(YANDEX_ReturnedGoods::getQuantityReturned)));
    }

    public static Map<String, Double> getMapPlacingOnShowcase(Sheet sheet) {
        List<YANDEX_ShowPlacement> list = getShowPlacement(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_ShowPlacement::getSku,
                        Collectors.summingDouble(YANDEX_ShowPlacement::getServiceCost)));
    }

    public static Map<String, Double> getMapDeliveryToConsumer(Sheet sheet) {
        List<YANDEX_DeliveryCustomer> list = getDeliveryCustomer(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveryCustomer::getSku,
                        Collectors.summingDouble(YANDEX_DeliveryCustomer::getServiceCost)));
    }

    public static Map<String, Double> getMapAcceptAndTransferPayment(Sheet sheetAccept, Sheet sheetTrans) {
        CompletableFuture<List<YANDEX_AcceptingPayment>> acceptPaymentsFuture = CompletableFuture.supplyAsync(() -> getAcceptingPayment(sheetAccept));
        CompletableFuture<List<YANDEX_TransferPayment>> transferPaymentsFuture = CompletableFuture.supplyAsync(() -> getTransferPayment(sheetTrans));

        try {
            List<YANDEX_AcceptingPayment> listAccept = acceptPaymentsFuture.get();
            List<YANDEX_TransferPayment> listTrans = transferPaymentsFuture.get();

            Map<String, Double> result = new ConcurrentHashMap<>();

            listAccept.parallelStream()
                    .collect(Collectors.groupingBy(YANDEX_AcceptingPayment::getSku,
                            Collectors.summingDouble(YANDEX_AcceptingPayment::getServiceCost)))
                    .forEach((sku, cost) -> result.merge(sku, cost, Double::sum));

            listTrans.parallelStream()
                    .collect(Collectors.groupingBy(YANDEX_TransferPayment::getSku,
                            Collectors.summingDouble(YANDEX_TransferPayment::getServiceCost)))
                    .forEach((sku, cost) -> result.merge(sku, cost, Double::sum));

            return result;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while processing payment data", e);
        }
    }

    public static Map<String, Double> getMapLoyaltyProgram(Sheet sheet) {
        List<YANDEX_LoyaltyProgram> list = getLoyaltyProgram(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_LoyaltyProgram::getSku,
                        Collectors.summingDouble(YANDEX_LoyaltyProgram::getServiceCost)));
    }

    public static Map<String, Double> getMapBoostSales(Sheet sheet) {
        List<YANDEX_BoostSales> list = getBoostSales(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_BoostSales::getSku,
                        Collectors.summingDouble(YANDEX_BoostSales::getPostPayment)));
    }

    public static Double getSumShelves(Sheet sheet) {
        List<YANDEX_Shelves> list = getShelves(sheet);

        return list.stream()
                .mapToDouble(YANDEX_Shelves::getServiceCost)
                .sum();
    }

    public static Map<String, Double> calculateServiceCostRatio(Sheet deliverySheet, Sheet returnSheet, Sheet serviceSheet) {
        CompletableFuture<Double> totalServiceCostFuture = CompletableFuture.supplyAsync(() -> getSumShelves(serviceSheet));
        CompletableFuture<Map<String, Integer>> deliveryReturnDifferenceFuture = CompletableFuture.supplyAsync(() -> getDeliveryReturnDifference(deliverySheet, returnSheet));

        try {
            Double totalServiceCost = totalServiceCostFuture.get();
            Map<String, Integer> deliveryReturnDifference = deliveryReturnDifferenceFuture.get();

            Integer totalDeliveryReturnDifference = deliveryReturnDifference.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();

            Map<String, Double> resultMap = new ConcurrentHashMap<>();

            if (totalDeliveryReturnDifference == 0) {
                deliveryReturnDifference.forEach((sku, difference) -> resultMap.put(sku, 0.0));
            } else {
                deliveryReturnDifference.forEach((sku, difference) -> {
                    Double ratio = (totalServiceCost / totalDeliveryReturnDifference) * difference;
                    resultMap.put(sku, ratio);
                });
            }

            return resultMap;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while processing data", e);
        }
    }

    public static Map<String, Integer> getDeliveryReturnDifference(Sheet deliverySheet, Sheet returnSheet) {
        CompletableFuture<Map<String, Integer>> deliveryCountFuture = CompletableFuture.supplyAsync(() -> getMapDeliveryCount(deliverySheet));
        CompletableFuture<Map<String, Integer>> returnCountFuture = CompletableFuture.supplyAsync(() -> getMapReturnCount(returnSheet));

        try {
            Map<String, Integer> deliveryCount = deliveryCountFuture.get();
            Map<String, Integer> returnCount = returnCountFuture.get();

            Map<String, Integer> differenceMap = new ConcurrentHashMap<>();

            deliveryCount.forEach((sku, delivery) -> {
                Integer returns = returnCount.getOrDefault(sku, 0);
                differenceMap.put(sku, delivery - returns);
            });

            returnCount.forEach((sku, returns) -> {
                if (!differenceMap.containsKey(sku)) {
                    differenceMap.put(sku, -returns);
                }
            });

            return differenceMap;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while processing data", e);
        }
    }
}
