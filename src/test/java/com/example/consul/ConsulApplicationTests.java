package com.example.consul;

import com.example.consul.api.OZON_Api;
import com.example.consul.document.Excel;
import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.dto.OZON.OZON_SkuProductsReport;
import com.example.consul.dto.OZON.OZON_TransactionReport;
import com.example.consul.mapping.ListToHtml;
import com.example.consul.mapping.OZON_dataProcessing;
import com.example.consul.models.ApiKey;
import org.apache.commons.math3.analysis.function.Abs;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        ArrayList<OZON_TransactionReport.Operation> operations = new ArrayList<>();

        OZON_TransactionReport reports = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z", opT, "all",1,1000);
        operations.addAll(reports.getResult().getOperations());

        OZON_TransactionReport reports2 = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z", opT, "all",2,1000);
        operations.addAll(reports2.getResult().getOperations());

        Long sku1 = 477040103L;
        Long sku2 = 477040104L;

        List<OZON_TransactionReport.Operation> filtered = operations.stream()
                .filter(x -> x.getItems().size() == 0).toList();

        double total = operations.stream()
                .filter(x -> x.getItems().size() > 0)
                .filter(x -> Objects.equals(x.getItems().get(0).getSku(), sku1)|| Objects.equals(x.getItems().get(0).getSku(), sku2))
                .flatMap(o -> o.getServices().stream())
                .filter(s -> "MarketplaceServiceItemDelivToCustomer".equals(s.getName()))
                .mapToDouble(OZON_TransactionReport.Service::getPrice)
                .sum();

        //operations.stream().collect(Co)

        System.out.println(total);
    }

    @Test
    void allOfferIdWithSku(){
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        OZON_DetailReport report =  api.getDetailReport("2024-01");
        List<OZON_DetailReport.Row> rows = report.getResult().getRows();
        Set<String> offersId = OZON_dataProcessing.groupByOfferId(rows).keySet();

        String[] ids = offersId.toArray(new String[0]);
        OZON_SkuProductsReport products = api.getProductInfoByOfferId(ids);

        System.out.println(products.getSkuListByOfferId());
    }

    @Test
    void Aq(){
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        OZON_DetailReport report =  api.getDetailReport("2024-01");
        List<OZON_DetailReport.Row> rows = report.getResult().getRows();
        Set<String> offersId = OZON_dataProcessing.groupByOfferId(rows).keySet();

        OZON_SkuProductsReport products = api.getProductInfoByOfferId(offersId.toArray(new String[0]));
        Map<String, List<Long>> offerSku = products.getSkuListByOfferId();

        ArrayList<String> opT = new ArrayList<>();
        opT.add("MarketplaceRedistributionOfAcquiringOperation");

        OZON_TransactionReport rp = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all",1,1000);
        List<OZON_TransactionReport.Operation> operations = rp.getResult().getOperations();

        rp = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all",2,1000);

        operations.addAll(rp.getResult().getOperations());

        Map<Long, Double> skuPrice = operations.stream().collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSku,
                Collectors.summingDouble(OZON_TransactionReport.Operation::getPrice)));


        Map<String,Double> map2 = offerSku.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                        .mapToDouble(row -> skuPrice.entrySet().stream().filter(o -> o.getKey().equals(row))
                                .mapToDouble(Map.Entry::getValue).sum())
                        .sum()
        ));

        System.out.println(map2);
    }


    @Test
    void getProductInfo(){
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
    void DetailReportTest(){
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        OZON_DetailReport report =  api.getDetailReport("2024-01");
        List<OZON_DetailReport.Row> rows = report.getResult().getRows();
        double sum = 0;
        for(OZON_DetailReport.Row row: rows){
            if(row.getOffer_id().equals("RO010"))

                sum += row.getPrice() * row.getSale_qty();
        }
    }

    @Test
    void TransactionReportTest(){
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");
        ArrayList<String> operationTypes=new ArrayList<>();
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
        Excel excel = new Excel();
        excel.createExcel("test.xls");
    }

}
