package com.example.consul.mapping;

import com.example.consul.mapping.sheets.*;
import org.apache.poi.ss.usermodel.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.consul.mapping.excelProcessing.DataFromExcel.*;

public class YANDEX_dataProcessing {
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
    public Map<String, Double> getMapMarketplaceDiscount(Sheet sheet) {
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
