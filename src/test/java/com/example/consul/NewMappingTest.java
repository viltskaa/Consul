package com.example.consul;

import com.example.consul.dto.OZON.OZON_SkuProductsReport;
import com.example.consul.dto.OZON.OZON_TransactionReport;
import com.example.consul.mapping.OZON_dataProcessing;
import com.example.consul.services.OZON_Service;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootTest
public class NewMappingTest {
    @Autowired
    private OZON_Service ozonService;

    @Test
    public void newMapping() {
//        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof
        ozonService.setHeaders("4670697c-2557-432b-bc5e-8979d12b3618", "633752"); //Zastole
//        ozonService.setHeaders("2bdf5f47-2351-4b4a-8303-896be2fd80c6","1380673"); // Alica
//        ozonService.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423"); // Alica_2

        Pair<String, String> pairDate = ozonService.getDate(2024, 6);
        List<String> oper = new ArrayList<>();
        OZON_TransactionReport report = ozonService.getTransactionReport(pairDate.a, pairDate.b, oper, "all");

        // получаем список операций
        List<OZON_TransactionReport.Operation> operations = report.getResult().getOperations();

        // фильтруем по нужному нам сервису и группируем по ску, суммируя для ску цену за нужный сервис
        Map<Long, Double> collect = operations.stream()
                .filter(operation -> operation.checkServiceName("MarketplaceRedistributionOfAcquiringOperation"))
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceRedistributionOfAcquiringOperation"))));


        Map<String, Long> offerSku = ozonService.getProductInfoBySku(
                        OZON_dataProcessing.getSkus(operations)
                ).getResult()
                // получаем информация по всем ску, которые были выявлены в транзакциях
                .getItems()
                .stream()
                // приводим инфу к мапу, где ключ это оффер айди, а значение ску
                .collect(Collectors.toMap(
                        OZON_SkuProductsReport.OZON_SkuProduct::getOffer_id,
                        OZON_SkuProductsReport.OZON_SkuProduct::getSku
                ));

//        Map<String, Double> res = OZON_dataProcessing.sumLastMile_(offerSku, operations);
//
        double sum = collect.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        System.out.println(sum);
//
//        res.forEach((k, v) -> System.out.println(k + " : " + v));
    }
    //-43012.32
    // 732.7

    @Test
    public void testOneArticle(){
        //        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof
        ozonService.setHeaders("4670697c-2557-432b-bc5e-8979d12b3618", "633752"); //Zastole
//        ozonService.setHeaders("2bdf5f47-2351-4b4a-8303-896be2fd80c6","1380673"); // Alica
//        ozonService.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423"); // Alica_2

        Pair<String, String> pairDate = ozonService.getDate(2024, 6);
        List<String> oper = new ArrayList<>();
        OZON_TransactionReport report = ozonService.getTransactionReport(pairDate.a, pairDate.b, oper, "all");

        // получаем список операций
        List<OZON_TransactionReport.Operation> operations = report.getResult().getOperations();

        double sum = 0;
        for (OZON_TransactionReport.Operation operation : operations) {
            // 731565806  MDF01R139E16 разница 336,89 (в нашу больше)
            // 732108456  MDF02R139O16 сходится
            //
//            if(operation.getSkuNoNull() == 732108456L){
                for (OZON_TransactionReport.Service service : operation.getServices()) {
                    if (Objects.equals(service.getName(), "MarketplaceServiceItemDelivToCustomer")) {
//                        System.out.println(operation.getOperation_type_name());
//                        System.out.println(service.getPrice());
//                        System.out.println(operation.getAllServicesName());
//                        System.out.println();
                        if(operation.getItems().isEmpty()) {
                                                    System.out.println(operation.getOperation_type_name());
//                        System.out.println(service.getPrice());
                        System.out.println(operation.getAllServicesName());
                            sum += service.getPrice();
                        }
                    }
                }
//            }
        }
        System.out.println(sum);
    }

    @Test
    public void test(){
        //        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof
        ozonService.setHeaders("4670697c-2557-432b-bc5e-8979d12b3618", "633752"); //Zastole
//        ozonService.setHeaders("2bdf5f47-2351-4b4a-8303-896be2fd80c6","1380673"); // Alica
//        ozonService.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423"); // Alica_2

        Pair<String, String> pairDate = ozonService.getDate(2024, 6);
        List<String> oper = new ArrayList<>();
        OZON_TransactionReport report = ozonService.getTransactionReport(pairDate.a, pairDate.b, oper, "all");

        // получаем список операций
        List<OZON_TransactionReport.Operation> operations = report.getResult().getOperations();

        // фильтруем по нужному нам сервису и группируем по ску, суммируя для ску цену за нужный сервис
        Map<Long, Double> collect = operations.stream()
                .filter(operation -> operation.checkServiceName("MarketplaceRedistributionOfAcquiringOperation"))
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSkuNoNull,
                        Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceRedistributionOfAcquiringOperation"))));


        Map<Long, String> offerSku = ozonService.getProductInfoBySku(
                        OZON_dataProcessing.getSkus(operations)
                ).getResult()
                // получаем информация по всем ску, которые были выявлены в транзакциях
                .getItems()
                .stream()
                // приводим инфу к мапу, где ключ это оффер айди, а значение ску
                .collect(Collectors.toMap(
                        OZON_SkuProductsReport.OZON_SkuProduct::getSku,
                        OZON_SkuProductsReport.OZON_SkuProduct::getOffer_id
                ));

        Map<String, Double> res = OZON_dataProcessing.sumLastMile(offerSku, operations);

        double sum = res.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        System.out.println(" sumLastMile " + sum);
//
//        sum = collect.values()
//                .stream()
//                .mapToDouble(Double::doubleValue)
//                .sum();
//        System.out.println("collect sumCashbackIndividualPoints" + sum);

    }

    @Test
    public void testLastMile() {
        //        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof
        ozonService.setHeaders("4670697c-2557-432b-bc5e-8979d12b3618", "633752"); //Zastole
//        ozonService.setHeaders("2bdf5f47-2351-4b4a-8303-896be2fd80c6","1380673"); // Alica
//        ozonService.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423"); // Alica_2

//        Pair<String, String> pairDate = ozonService.getDate(2024, 6
//        );
//        List<String> oper = new ArrayList<>();
//        OZON_TransactionReport report = ozonService.getTransactionReport(pairDate.a, pairDate.b, oper, "all");
//
//        // получаем список операций
//        List<OZON_TransactionReport.Operation> operations = report.getResult().getOperations();
//
//        List<OZON_TransactionReport.Operation> filtered = operations.stream()
//                .filter(x -> x.getItems().size() == 0).toList();
//        Map<String, List<OZON_TransactionReport.Operation>> groupByPostingNumber = operations
//                .stream()
//                .filter(x -> x.getPosting() != null)
//                .map(OZON_TransactionReport.Operation::of)
//                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getPostingNumber));
//        Map<String, List<OZON_TransactionReport.Operation>> getBySku = groupByPostingNumber
//                .entrySet()
//                .stream()
//                .filter(entry -> entry.getValue().stream()
//                        .anyMatch(y -> y.hasSku(sku1) || y.hasSku(sku2)))
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue
//                ));
//        double total = getBySku.values().stream()
//                .flatMap(List::stream)
//                .filter(s -> s.getServices() != null && s.getServices().stream()
//                        .anyMatch(x -> "MarketplaceServiceItemDelivToCustomer".equals(x.getName())))
//                .mapToDouble(x -> x.getServices().stream()
//                        .filter(y -> "MarketplaceServiceItemDelivToCustomer".equals(y.getName()))
//                        .mapToDouble(serv -> serv.getPrice())
//                        .sum())
//                .sum();
//        System.out.println(total);
    }
}
