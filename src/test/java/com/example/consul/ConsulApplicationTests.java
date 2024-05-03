package com.example.consul;

import com.example.consul.api.OZON_Api;
import com.example.consul.api.OZON_PerformanceApi;
import com.example.consul.document.ExcelBuilder;
import com.example.consul.document.configurations.ExcelConfig;
import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.dto.OZON.OZON_SkuProductsReport;
import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.dto.OZON.OZON_TransactionReport;
import com.example.consul.mapping.ListToHtml;
import com.example.consul.mapping.OZON_dataProcessing;
import com.example.consul.models.ApiKey;
import com.example.consul.services.ExcelService;
import com.example.consul.services.OZON_Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
class ConsulApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void Transactions() {
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");

        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationAgentStornoDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        OZON_TransactionReport reports = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z", opT, "all", 1, 1000);
        ArrayList<OZON_TransactionReport.Operation> operations = new ArrayList<>(reports.getResult().getOperations());

        OZON_TransactionReport reports2 = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z", opT, "all", 2, 1000);
        operations.addAll(reports2.getResult().getOperations());

        OZON_DetailReport report = api.getDetailReport(1,2024);

        List<OZON_DetailReport.Row> rows = report.getResult().getRows();
        Set<String> offersId = OZON_dataProcessing.groupByOfferId(rows).keySet();

        OZON_SkuProductsReport products = api.getProductInfoByOfferId(offersId.toArray(new String[0]));
        Map<String, List<Long>> offerSku = products.getSkuListByOfferId();

        /*Map<String,List<OZON_TransactionReport.Operation>> getBySku = groupByPostingNumber
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(y->y.hasSku(sku1) || y.hasSku(sku2)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));*/

        /*double total = getBySku.values().stream()
                .flatMap(List::stream)
                .filter(s -> s.getServices() != null && s.getServices().stream()
                        .anyMatch(x -> "MarketplaceServiceItemDelivToCustomer".equals(x.getName())))
                .mapToDouble(x -> x.getServices().stream()
                        .filter(y -> "MarketplaceServiceItemDelivToCustomer".equals(y.getName()))
                        .mapToDouble(serv -> serv.getPrice())
                        .sum())
                        .sum();*/

        System.out.println(OZON_dataProcessing.sumLastMile(offerSku, operations));
    }


    //Логистика. Готово
    @Test
    void testLogistic() {
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");

        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        List<OZON_TransactionReport.Operation> operations = new ArrayList<>();
        IntStream.rangeClosed(1, 2).forEach(i -> {
            OZON_TransactionReport report = api.getTransactionReport(
                    "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                    opT, "all", i, 1000);
            operations.addAll(report.getResult().getOperations());
        });

        Long sku1 = 477053081L;
        Long sku2 = 477053086L;

        OZON_DetailReport report = api.getDetailReport(1,2024);

        List<OZON_DetailReport.Row> rows = report.getResult().getRows();
        Set<String> offersId = OZON_dataProcessing.groupByOfferId(rows).keySet();

        OZON_SkuProductsReport products = api.getProductInfoByOfferId(offersId.toArray(new String[0]));
        Map<String, List<Long>> offerSku = products.getSkuListByOfferId();
        System.out.println(OZON_dataProcessing.sumLogistic(offerSku, operations));
    }

    // обработка отправления
    @Test
    void testGetDropOffSC() {
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");

        //OperationReturnGoodsFBSofRMS — доставка и обработка возврата, отмены, невыкупа
        //OperationAgentDeliveredToCustomer - Доставка покупателю
        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        OZON_TransactionReport report = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all", 1, 1000);
        List<OZON_TransactionReport.Operation> operations = new ArrayList<>(report.getResult().getOperations());

        OZON_TransactionReport report2 = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all", 2, 1000);
        operations.addAll(report2.getResult().getOperations());

        Long sku1 = 477053081L;
        Long sku2 = 477053086L;

//        Long sku1 = 477040103L;
//        Long sku2 = 477040104L;

        double sum = 0;
        for (OZON_TransactionReport.Operation op : operations) {

            if (op.getSku().equals(sku1) || op.getSku().equals(sku2)) {
                if (op.getPriceByServiceName("MarketplaceServiceItemDropoffSC") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemDropoffSC");
                }
                if (op.getPriceByServiceName("MarketplaceServiceItemDropoffPVZ") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemDropoffPVZ");
                }
            }
        }
        OZON_DetailReport report5 = api.getDetailReport(1,2024);

        List<OZON_DetailReport.Row> rows = report5.getResult().getRows();
        Set<String> offersId = OZON_dataProcessing.groupByOfferId(rows).keySet();

        OZON_SkuProductsReport products = api.getProductInfoByOfferId(offersId.toArray(new String[0]));
        Map<String, List<Long>> offerSku = products.getSkuListByOfferId();
        System.out.println(OZON_dataProcessing.sumShipmentProcessing(offerSku, operations));
    }

    // Обработка возврата
    @Test
    void testGetRefundProcessing() {
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");

        //OperationReturnGoodsFBSofRMS — доставка и обработка возврата, отмены, невыкупа
        //OperationAgentDeliveredToCustomer - Доставка покупателю
        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        OZON_TransactionReport report = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all", 1, 1000);
        List<OZON_TransactionReport.Operation> operations = new ArrayList<>(report.getResult().getOperations());

        OZON_TransactionReport report2 = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all", 2, 1000);
        operations.addAll(report2.getResult().getOperations());

        Long sku1 = 477053081L;
        Long sku2 = 477053086L;

//        Long sku1 = 477040103L;
//        Long sku2 = 477040104L;

        double sum = 0;
        for (OZON_TransactionReport.Operation op : operations) {

            if (op.getSku().equals(sku1) || op.getSku().equals(sku2)) {
                if (op.getPriceByServiceName("MarketplaceServiceItemRedistributionReturnsPVZ") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemRedistributionReturnsPVZ");
                }
                if (op.getPriceByServiceName("MarketplaceServiceItemReturnNotDelivToCustomer") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemReturnNotDelivToCustomer");
                }
                if (op.getPriceByServiceName("MarketplaceServiceItemReturnPartGoodsCustomer") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemReturnPartGoodsCustomer");
                }
            }
        }
        OZON_DetailReport report5 = api.getDetailReport(1,2024);

        List<OZON_DetailReport.Row> rows = report5.getResult().getRows();
        Set<String> offersId = OZON_dataProcessing.groupByOfferId(rows).keySet();

        OZON_SkuProductsReport products = api.getProductInfoByOfferId(offersId.toArray(new String[0]));
        Map<String, List<Long>> offerSku = products.getSkuListByOfferId();
        System.out.println(OZON_dataProcessing.sumReturnProcessing(offerSku, operations));
    }

    //Доставка возврата
    @Test
    void testGetReturnShipping() {
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");

        //OperationReturnGoodsFBSofRMS — доставка и обработка возврата, отмены, невыкупа
        //OperationAgentDeliveredToCustomer - Доставка покупателю
        ArrayList<String> opT = new ArrayList<>();
//        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        OZON_TransactionReport report = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all", 1, 1000);
        List<OZON_TransactionReport.Operation> operations = new ArrayList<>(report.getResult().getOperations());

        OZON_TransactionReport report2 = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all", 2, 1000);
        operations.addAll(report2.getResult().getOperations());

        Long sku1 = 477053081L;
        Long sku2 = 477053086L;

//        Long sku1 = 477040103L;
//        Long sku2 = 477040104L;

        double sum = 0;
        for (OZON_TransactionReport.Operation op : operations) {

            if ((op.getSku().equals(sku1) || op.getSku().equals(sku2)) &&
                    !op.checkServiceName("MarketplaceServiceItemDropoffSC") &&
                    !op.checkServiceName("MarketplaceServiceItemDropoffPVZ")
            ) {
                if (op.getPriceByServiceName("MarketplaceServiceItemReturnFlowLogistic") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemReturnFlowLogistic");
                }
                if (op.getPriceByServiceName("MarketplaceServiceItemDirectFlowLogistic") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemDirectFlowLogistic");
                }
            }
        }
        OZON_DetailReport report5 = api.getDetailReport(1,2024);

        List<OZON_DetailReport.Row> rows = report5.getResult().getRows();
        Set<String> offersId = OZON_dataProcessing.groupByOfferId(rows).keySet();

        OZON_SkuProductsReport products = api.getProductInfoByOfferId(offersId.toArray(new String[0]));
        Map<String, List<Long>> offerSku = products.getSkuListByOfferId();
        System.out.println(OZON_dataProcessing.sumReturnDelivery(offerSku, operations));
    }

    @Test
    void getDateMonthName() {
        String dateStr = "2023-01";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

        try {
            Date date = dateFormat.parse(dateStr);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLLL yyyy");
            String monthAndYear = simpleDateFormat.format(date).toUpperCase();

            System.out.println(monthAndYear);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }


    @Test
    void allOfferIdWithSku() {
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        OZON_DetailReport report = api.getDetailReport(1,2024);
        List<OZON_DetailReport.Row> rows = report.getResult().getRows();
        Set<String> offersId = OZON_dataProcessing.groupByOfferId(rows).keySet();

        String[] ids = offersId.toArray(new String[0]);
        OZON_SkuProductsReport products = api.getProductInfoByOfferId(ids);

        System.out.println(products.getSkuListByOfferId());
    }

    @Test
    void Aq() {
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        OZON_DetailReport report = api.getDetailReport(1,2024);

        List<OZON_DetailReport.Row> rows = report.getResult().getRows();
        Set<String> offersId = OZON_dataProcessing.groupByOfferId(rows).keySet();

        OZON_SkuProductsReport products = api.getProductInfoByOfferId(offersId.toArray(new String[0]));
        Map<String, List<Long>> offerSku = products.getSkuListByOfferId();

        ArrayList<String> opT = new ArrayList<>();
        opT.add("MarketplaceRedistributionOfAcquiringOperation");

        OZON_TransactionReport rp = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all", 1, 1000);
        List<OZON_TransactionReport.Operation> operations = rp.getResult().getOperations();

        rp = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all", 2, 1000);

        operations.addAll(rp.getResult().getOperations());

        Map<Long, Double> skuPrice = operations.stream().collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSku,
                Collectors.summingDouble(OZON_TransactionReport.Operation::getPrice)));


        Map<String, Double> map2 = offerSku.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                        .mapToDouble(row -> skuPrice.entrySet().stream().filter(o -> o.getKey().equals(row))
                                .mapToDouble(Map.Entry::getValue).sum())
                        .sum()
        ));

        System.out.println(map2);
    }


    @Test
    void getProductInfo() {
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        List<Long> skus = new ArrayList<>();
        skus.add(477040103L);
        skus.add(477056233L);

        OZON_SkuProductsReport report = api.getProductInfo(skus);
        System.out.println(report.findBySku(477040103L).getOffer_id());
        System.out.println(report.getSkuListByOfferId());

    }

    @Test
    void DetailReportTest() {
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        OZON_DetailReport report = api.getDetailReport(1,2024);
        List<OZON_DetailReport.Row> rows = report.getResult().getRows();
        double sum = 0;
        for (OZON_DetailReport.Row row : rows) {
            if (row.getItem().getOffer_id().equals("RO010"))

                sum += row.getSeller_price_per_instance() * row.getDelivery_commission().getQuantity();
        }
    }

    @Test
    void TransactionReportTest() {
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        ArrayList<String> operationTypes = new ArrayList<>();
        operationTypes.add("MarketplaceRedistributionOfAcquiringOperation");
//        OZON_TransactionReport report =  api.getTransactionReport("2024-01-01T00:00:00.000Z","2024-01-31T00:00:00.000Z", operationTypes,"other");
    }

    @Test
    void HtmlBuildTest() {
        ArrayList<ApiKey> test = new ArrayList<>();
        test.add(new ApiKey(0L, "OZON", "0312756387"));
        test.add(new ApiKey(1L, "OZON_1", "9765"));
        test.add(new ApiKey(2L, "WB", "6789"));
        ListToHtml.build(test);
    }

    @Test
    void ExcelFile() throws IOException {
//        Excel excel = new Excel(new ExcelService(new OZON_Service(new OZON_Api(), new OZON_PerformanceApi())));
//        excel.createExcel("2023-11.xls", "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
//                "350423","2023-11","2023-11-01T00:00:00.000Z","2023-11-30T23:59:59.999Z");
//        excel.createExcel("2023-12.xls", "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
//                "350423","2023-12","2023-12-01T00:00:00.000Z","2023-12-31T23:59:59.999Z");
//        excel.createExcel("2024-01.xls", "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
//                "350423","2024-01","2024-01-01T00:00:00.000Z","2024-01-31T23:59:59.999Z");
//        excel.createExcel("2024-02.xls", "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
//                "350423","2024-02","2024-02-01T00:00:00.000Z","2024-02-29T23:59:59.999Z");
//        excel.createExcel("2024-03.xls", "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
//                "350423","2024-03","2024-03-01T00:00:00.000Z","2024-03-31T23:59:59.999Z");
//
//        excel.createExcel("2024-01.xls",
//                "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
//                "350423",
//                "27013136-1713353681106@advertising.performance.ozon.ru",
//                "w8jTBuPxzAr5iW2dvioeroGh_7aDVHOyS8LhwD4lzK2x5kUQeYytrJ7HeD4yEygPU2iAO9AaU-XOdV7Z1Q",
//                "2024-01",
//                "2024-01-01T00:00:00.000Z",
//                "2024-01-31T23:59:59.999Z");
    }

    @Test
    void NewExcelCreateTest() throws IOException {
        ExcelService excelService = new ExcelService(new OZON_Service(new OZON_Api(), new OZON_PerformanceApi()));

        List<OZON_TableRow> data = excelService.mergeMapsToTableRows(
                "ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66",
                "350423",
                "27013136-1713353681106@advertising.performance.ozon.ru",
                "w8jTBuPxzAr5iW2dvioeroGh_7aDVHOyS8LhwD4lzK2x5kUQeYytrJ7HeD4yEygPU2iAO9AaU-XOdV7Z1Q",
                1, 2024);

        ExcelBuilder.createDocument(new ExcelConfig<>(
                "2024_01.xls",
                "1",
                "",
                data
        ));
    }
}
