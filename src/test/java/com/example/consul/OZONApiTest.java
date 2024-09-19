package com.example.consul;

import com.example.consul.api.OZON_Api;
import com.example.consul.document.v1.ExcelBuilderV1;
import com.example.consul.document.v1.configurations.ExcelConfig;
import com.example.consul.document.v1.configurations.HeaderConfig;
import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.dto.OZON.OZON_SkuProductsReport;
import com.example.consul.dto.OZON.OZON_TransactionReport;
import com.example.consul.services.OZON_Service;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class OZONApiTest {
    @Autowired
    private OZON_Service ozonService;

    @Autowired
    private OZON_Api api;

    @Test
    public void newMapping() {
        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof
//        ozonService.setHeaders("4670697c-2557-432b-bc5e-8979d12b3618", "633752"); //Zastole
//        ozonService.setHeaders("2bdf5f47-2351-4b4a-8303-896be2fd80c6","1380673"); // Alica
//        ozonService.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423"); // Alica_2

        Pair<String, String> pairDate = ozonService.getDate(2024, 6);
        List<String> oper = new ArrayList<>();
        OZON_TransactionReport report = ozonService.getTransactionReport(pairDate.a, pairDate.b, oper, "all");
        List<OZON_TransactionReport.Operation> operations = report.getResult().getOperations().stream()
                .filter(operation -> operation.checkServiceName("MarketplaceRedistributionOfAcquiringOperation")).toList();

        Map<Long, Double> collect = operations.stream().collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSku2,
                Collectors.summingDouble(val -> val.getPriceByServiceNameNoNull("MarketplaceRedistributionOfAcquiringOperation"))));

        double sum = collect.values().stream().mapToDouble(Double::doubleValue).sum();
        System.out.println(sum);
    }

    @Test
    public void skuTest() {
//        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof
//        ozonService.setHeaders("4670697c-2557-432b-bc5e-8979d12b3618", "633752"); //Zastole
//        ozonService.setHeaders("2bdf5f47-2351-4b4a-8303-896be2fd80c6","1380673"); // Alica
        ozonService.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423"); // Alica_2
        List<String> offerIds = ozonService.getListOfferIdByDate(6, 2024);
        OZON_SkuProductsReport report = ozonService.getProductInfoByOfferId(offerIds);

        System.out.println("start");

        for (OZON_SkuProductsReport.OZON_SkuProduct product : report.getResult().getItems()) {
            if (!product.getSources().isEmpty()) {
                System.out.println(product.getOffer_id());
            }
        }
    }

    @Test
    public void skuTest2() {
        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof
//        ozonService.setHeaders("4670697c-2557-432b-bc5e-8979d12b3618", "633752"); //Zastole
//        ozonService.setHeaders("2bdf5f47-2351-4b4a-8303-896be2fd80c6","1380673"); // Alica
        ozonService.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423"); // Alica_2

        Pair<String, String> pairDate = ozonService.getDate(2024, 6);
        List<String> oper = new ArrayList<>();
        OZON_TransactionReport report = ozonService.getTransactionReport(pairDate.a, pairDate.b, oper, "all");
        List<OZON_TransactionReport.Operation> operations = report.getResult().getOperations();

        Map<Long, Long> collect = operations.stream().collect(Collectors.groupingBy(OZON_TransactionReport.Operation::getSku2, Collectors.counting()));
        collect.forEach((k, v) -> System.out.println(k));
    }


    @Test
    public void shipmentProcessing() {
//        ozonService.setHeaders("4670697c-2557-432b-bc5e-8979d12b3618", "633752");
        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof

        Pair<String, String> pairDate = ozonService.getDate(2024, 6);
        List<String> oper = new ArrayList<>();
//        oper.add("OperationReturnGoodsFBSofRMS");
        OZON_TransactionReport report = ozonService.getTransactionReport(pairDate.a, pairDate.b, oper, "all");
        List<OZON_TransactionReport.Operation> operations = report.getResult().getOperations();
        double sum = 0;
        for (OZON_TransactionReport.Operation operation : operations) {
            for (OZON_TransactionReport.Service service : operation.getServices()) {

                if (
                        (Objects.equals(service.getName(), "MarketplaceServiceItemDropoffPVZ") ||
                                Objects.equals(service.getName(), "MarketplaceServiceItemDropoffSC"))
                ) {
//                    System.out.println(operation.getItems().getFirst().getSku());
                    sum += service.getPrice();
                }
            }
        }
//        Double res = operations.stream()
//                .mapToDouble(item -> item.getPriceByServiceNameNoNull("MarketplaceServiceItemDropoffSC") +
//                        item.getPriceByServiceNameNoNull("MarketplaceServiceItemDropoffPVZ")).sum();

        Double res = operations.stream()
//                .filter(op -> op.getItems().isEmpty())
//                .filter(op -> op.getPriceByServiceName("MarketplaceRedistributionOfAcquiringOperation") != null)
                .mapToDouble(item -> item.getPriceByServiceNameNoNull("MarketplaceRedistributionOfAcquiringOperation")).sum();

//        System.out.println(sum);
        System.out.println(res);
    }

    @Test
    public void generateAlica_2() throws IOException {
        List<OZON_TableRow> data = ozonService.getData(
                "9e98a805-4717-4ea4-a852-41ed1e5948ac",
                "350423",
                "29156444-1716905674931@advertising.performance.ozon.ru",
                "PSCVDiFfWGgV0rWcF6MrA_0gZAW8iyYXH6AFTDk5ZviS4JRETHIBXHLX0033IVnRzr106ULnGks5le2SPg",
                2024, 6);

        ExcelBuilderV1.createDocument(
                ExcelConfig.<OZON_TableRow>builder()
                        .fileName("Алиса2_OZON_июнь_2024.xls")
                        .header(
                                HeaderConfig.builder()
                                        .title("TEST")
                                        .description("NEW METHOD")
                                        .build()
                        )
                        .data(List.of(data))
                        .sheetsName(List.of("1"))
                        .build()
        );
    }

    @Test
    public void getMap() {
        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof
        System.out.println(ozonService.getOfferIdToSkuMap(4, 2024));
    }

    @Test
    public void generateAlica() throws IOException {
        List<OZON_TableRow> data = ozonService.getData(
                "2bdf5f47-2351-4b4a-8303-896be2fd80c6",
                "1380673",
                "27890997-1714654433641@advertising.performance.ozon.ru",
                "cPOjX52_rQEOZ6nDtHP6_6UUQyA1vpWu1Ku60-km7xVJAZPQE1Tduc1x0u3mtawa6wjM6uzNGyH2BZ4fiA",
                2024, 6);

        ExcelBuilderV1.createDocument(
                ExcelConfig.<OZON_TableRow>builder()
                        .fileName("Алиса_OZON_июнь_2024.xls")
                        .header(
                                HeaderConfig.builder()
                                        .title("TEST")
                                        .description("NEW METHOD")
                                        .build()
                        )
                        .data(List.of(data))
                        .sheetsName(List.of("1"))
                        .build()
        );
    }

    @Test
    public void generateStuloff() throws IOException {
        List<OZON_TableRow> data = ozonService.getData(
                "1b04be41-8998-4189-a0cf-d40f2edb9f93",
                "1380622",
                "27891105-1714654608961@advertising.performance.ozon.ru",
                "BmuHOndqm93S7x_dqpTVNqyfdQf6zGO5OOkrAn72cSDI3PR6sqDx_CyBnPe1CgQwAlMWfLpMFLAy-BhyeQ",
                2024, 6);

        ExcelBuilderV1.createDocument(
                ExcelConfig.<OZON_TableRow>builder()
                        .fileName("Стулоф_OZON_июнь_2024_1.xls")
                        .header(
                                HeaderConfig.builder()
                                        .title("TEST")
                                        .description("NEW METHOD")
                                        .build()
                        )
                        .data(List.of(data))
                        .sheetsName(List.of("1"))
                        .build()
        );
    }

    @Test
    public void generateZastole() throws IOException {
        List<OZON_TableRow> data = ozonService.getData(
                "4670697c-2557-432b-bc5e-8979d12b3618",
                "633752",
                "27890862-1714654096169@advertising.performance.ozon.ru",
                "zFRlltf5AJlgU5ro-y1JCnVPr7JZwCAsLI7Gh8RUiuKb2McOieo2NXB_E9wWSfVbJcIMyZntMa1t5uv-SQ",
                2024, 6);

        ExcelBuilderV1.createDocument(
                ExcelConfig.<OZON_TableRow>builder()
                        .fileName("застолье_OZON_июнь_2024.xls")
                        .header(
                                HeaderConfig.builder()
                                        .title("TEST")
                                        .description("NEW METHOD")
                                        .build()
                        )
                        .data(List.of(data))
                        .sheetsName(List.of("1"))
                        .build()
        );
    }

    //Услуга продвижения (бонусы продавца(-) - MarketplaceServicePremiumCashbackIndividualPoints
    // РАБОТАЕТ!!!!
    @Test
    public void indPoints() {
//        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof
        ozonService.setHeaders("4670697c-2557-432b-bc5e-8979d12b3618", "633752"); //Zastole
//        ozonService.setHeaders("2bdf5f47-2351-4b4a-8303-896be2fd80c6","1380673"); // Alica
//        ozonService.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac","350423"); // Alica_2
        Pair<String, String> pairDate = ozonService.getDate(2024, 6);
        System.out.println(pairDate);
        List<String> oper = new ArrayList<>();
//        oper.add("OperationMarketplaceServicePremiumCashbackIndividualPoints");
        OZON_TransactionReport report = ozonService.getTransactionReport(pairDate.a, pairDate.b, oper, "all");

        double sum = 0;
        List<OZON_TransactionReport.Operation> operations = report.getResult().getOperations();
        for (OZON_TransactionReport.Operation operation : operations) {


//            if (!operation.getItems().isEmpty() && operation.getItems().getFirst().getSku() == 730591736L) {
////                sum += operation.getAmount();
//                for (OZON_TransactionReport.Service service : operation.getServices()) {
//                    if (Objects.equals(service.getName(), "MarketplaceServicePremiumCashbackIndividualPoints")) {
//                        sum += service.getPrice();
//                    }
//                }
//            }
        }
        System.out.println(sum);
    }

    // приобретение отзывов - MarketplaceSaleReviewsOperation
    @Test
    public void testMarketplaceSaleReviewsOperation() {
//        ozonService.setHeaders("1b04be41-8998-4189-a0cf-d40f2edb9f93", "1380622"); //stulof
        ozonService.setHeaders("4670697c-2557-432b-bc5e-8979d12b3618", "633752"); //Zastole
//        ozonService.setHeaders("2bdf5f47-2351-4b4a-8303-896be2fd80c6","1380673"); // Alica
//        ozonService.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac","350423"); // Alica_2
        Pair<String, String> pairDate = ozonService.getDate(2024, 6);
        System.out.println(pairDate);
        List<String> oper = new ArrayList<>();
        oper.add("OperationMarketplaceServicePremiumCashbackIndividualPoints");
        OZON_TransactionReport report = ozonService.getTransactionReport(pairDate.a, pairDate.b, oper, "all");

        double sum = 0;
        List<OZON_TransactionReport.Operation> operations = report.getResult().getOperations();
        for (OZON_TransactionReport.Operation operation : operations) {
            sum += operation.getAmount();
        }
        System.out.println(sum);
    }

    @Test
    public void testRelatedSku() {
        OZON_Api api = new OZON_Api();
        api.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423");
        List<Long> skus = new ArrayList<>();
        skus.add(477040104L);

        System.out.println(api.getRelatedSku(skus));
    }

    @Test
    public void testDetailReport() {
        OZON_Api api = new OZON_Api();
        api.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423");

        System.out.println(api.getDetailReport(4, 2024));
    }

//    @Test
//    public void testDetailReport(){
//        OZON_Api api = new OZON_Api();
//        api.setHeaders("9e98a805-4717-4ea4-a852-41ed1e5948ac", "350423");
//
//        System.out.println(api.getDetailReport(4, 2024));
//    }
}
