package com.example.consul;

import com.example.consul.api.OZON_Api;
import com.example.consul.document.Excel;
import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.dto.OZON.OZON_SkuProductsReport;
import com.example.consul.dto.OZON.OZON_TransactionReport;
import com.example.consul.mapping.ListToHtml;
import com.example.consul.models.ApiKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

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
        opT.add("MarketplaceRedistributionOfAcquiringOperation");

        OZON_TransactionReport reports = api.getTransactionReport(
                "2024-01-01T00:00:00.000Z", "2024-01-31T00:00:00.000Z", opT, "all");
        Long sku1 = 477040103L;
        Long sku2 = 477040104L;
        double sum = 0;
        for (OZON_TransactionReport.Operation operation : reports.getResult().getOperations()) {
            Long sku = operation.getItems().get(0).getSku();
            if (sku.equals(sku1) || sku.equals(sku2)) {
                sum += operation.getAmount();
            }
        }
        System.out.println(sum);
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
        OZON_TransactionReport report =  api.getTransactionReport("2024-01-01T00:00:00.000Z","2024-01-31T00:00:00.000Z", operationTypes,"other");
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
