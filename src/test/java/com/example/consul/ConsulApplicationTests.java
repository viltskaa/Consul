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
        opT.add("OperationReturnGoodsFBSofRMS");

        ArrayList<OZON_TransactionReport.Operation> operations = new ArrayList<>();

        OZON_TransactionReport reports = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z", opT, "all",1,1000);
        operations.addAll(reports.getResult().getOperations());

        OZON_TransactionReport reports2 = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z", opT, "all",2,1000);
        operations.addAll(reports2.getResult().getOperations());

        Long sku1 = 477053081L;
        Long sku2 = 477053086L;

        List<OZON_TransactionReport.Operation> filtered = operations.stream()
                .filter(x -> x.getItems().size() == 0).toList();

        Map<String,List<OZON_TransactionReport.Operation>> groupByPostingNumber = operations
                .stream()
                .filter(x -> x.getPosting() != null)
                .map(OZON_TransactionReport.Operation::of)
                .collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getPostingNumber));

        Map<String,List<OZON_TransactionReport.Operation>> getBySku = groupByPostingNumber
                .entrySet()
                .stream()
                        .filter(entry -> entry.getValue().stream()
                                .anyMatch(y->y.hasSku(sku1) || y.hasSku(sku2)))
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue
                                ));

        double total = getBySku.values().stream()
                .flatMap(List::stream)
                .filter(s -> s.getServices() != null && s.getServices().stream()
                        .anyMatch(x -> "MarketplaceServiceItemDelivToCustomer".equals(x.getName())))
                .mapToDouble(x -> x.getServices().stream()
                        .filter(y -> "MarketplaceServiceItemDelivToCustomer".equals(y.getName()))
                        .mapToDouble(serv -> serv.getPrice())
                        .sum())
                        .sum();

        System.out.println(total);
    }

    @Test
    void testLogistic(){
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");

        //OperationReturnGoodsFBSofRMS — доставка и обработка возврата, отмены, невыкупа
        //OperationAgentDeliveredToCustomer - Доставка покупателю
        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        List<OZON_TransactionReport.Operation> operations = new ArrayList<>();
        OZON_TransactionReport report = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all",1,1000);
        operations.addAll(report.getResult().getOperations());

        OZON_TransactionReport report2 = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all",2,1000);
        operations.addAll(report2.getResult().getOperations());

        Long sku1 = 477053081L;
        Long sku2 = 477053086L;

        double sum = 0;
        for(OZON_TransactionReport.Operation op: operations){

            if( op.getSku().equals(sku1) ||  op.getSku().equals(sku2)){
                //логистика
                if (op.getPriceByServiceName("MarketplaceServiceItemDirectFlowLogistic") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemDirectFlowLogistic");
                }
                //логистика вРЦ;
                if (op.getPriceByServiceName("MarketplaceServiceItemDirectFlowLogisticVDC") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemDirectFlowLogisticVDC");
                }
            }
        }
        System.out.println(sum);
    }

    // обработка отправления
    @Test
    void testGetDropOffSC(){
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");

        //OperationReturnGoodsFBSofRMS — доставка и обработка возврата, отмены, невыкупа
        //OperationAgentDeliveredToCustomer - Доставка покупателю
        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        List<OZON_TransactionReport.Operation> operations = new ArrayList<>();
        OZON_TransactionReport report = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all",1,1000);
        operations.addAll(report.getResult().getOperations());

        OZON_TransactionReport report2 = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all",2,1000);
        operations.addAll(report2.getResult().getOperations());

        Long sku1 = 477053081L;
        Long sku2 = 477053086L;

//        Long sku1 = 477040103L;
//        Long sku2 = 477040104L;

        double sum = 0;
        for(OZON_TransactionReport.Operation op: operations){

            if( op.getSku().equals(sku1) ||  op.getSku().equals(sku2)){
                if (op.getPriceByServiceName("MarketplaceServiceItemDropoffSC") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemDropoffSC");
                }
                if (op.getPriceByServiceName("MarketplaceServiceItemDropoffPVZ") != null) {
                    sum += op.getPriceByServiceName("MarketplaceServiceItemDropoffPVZ");
                }
            }
        }
        System.out.println(sum);
    }

    // Обработка возврата
    @Test
    void testGetRefundProcessing(){
        final OZON_Api api = new OZON_Api();
        api.setHeaders("ace0b5ec-e3f6-4eb4-a9a6-33a1a5c84f66", "350423");

        //OperationReturnGoodsFBSofRMS — доставка и обработка возврата, отмены, невыкупа
        //OperationAgentDeliveredToCustomer - Доставка покупателю
        ArrayList<String> opT = new ArrayList<>();
        opT.add("OperationAgentDeliveredToCustomer");
        opT.add("OperationReturnGoodsFBSofRMS");

        List<OZON_TransactionReport.Operation> operations = new ArrayList<>();
        OZON_TransactionReport report = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all",1,1000);
        operations.addAll(report.getResult().getOperations());

        OZON_TransactionReport report2 = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z",
                opT, "all",2,1000);
        operations.addAll(report2.getResult().getOperations());

        Long sku1 = 477053081L;
        Long sku2 = 477053086L;

//        Long sku1 = 477040103L;
//        Long sku2 = 477040104L;

        double sum = 0;
        for(OZON_TransactionReport.Operation op: operations){

            if( op.getSku().equals(sku1) ||  op.getSku().equals(sku2)){
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
        System.out.println(sum);
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
        excel.createExcel("2024-02.xls", "2024-02");
//        excel.createExcel("2023-12.xls", "2023-12");
//        excel.createExcel("2023-11.xls", "2023-11");
    }

}
