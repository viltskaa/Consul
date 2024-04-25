package com.example.consul.api;


import com.example.consul.api.utils.ForTransactions;
import com.example.consul.dto.OZON.OZON_DetailReport;
import com.example.consul.dto.OZON.OZON_SkuProductsReport;
import com.example.consul.dto.OZON.OZON_TransactionReport;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class OZON_Api {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    public OZON_Api() {
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public void setHeaders(@NotNull String apiKey,@NotNull String clientId) {
        headers = new HttpHeaders();
        headers.add("Api-Key", apiKey);
        headers.add("Client-Id", clientId);
        headers.add("Content-Type", "application/json");
    }

    // Финансовые отчеты => Список транзакций
    @Nullable
    public OZON_TransactionReport getTransactionReport(@NotNull String from,
                                                             @NotNull String to,
                                                             @NotNull ArrayList<String> operation_type,
                                                             @NotNull String transaction_type,
                                                       @NotNull int page,
                                                       @NotNull int page_size){
        String transactionReportUrl = "https://api-seller.ozon.ru/v3/finance/transaction/list";

        HttpEntity<String> request = new HttpEntity<>
                (new Gson().toJson(new ForTransactions(from, to, operation_type, transaction_type, page,page_size)), headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(transactionReportUrl, request, String.class );
        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_TransactionReport.class);
        } else {
            return null;
        }
    }

    // Финансовые отчеты => Отчёт о реализации товаров
    @Nullable
    public OZON_DetailReport getDetailReport(@NotNull String date){
        String detailReportUrl = "https://api-seller.ozon.ru/v1/finance/realization";

        Map<String, String> map= new HashMap<>();
        map.put("date", date);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(detailReportUrl, request, String.class );

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_DetailReport.class);
        } else {
            return null;
        }
    }

    //Загрузка и обновление товаров => Получить список товаров по идентификаторам
    @Nullable
    public OZON_SkuProductsReport getProductInfo(List<Long> skus){
        String url = "https://api-seller.ozon.ru/v2/product/info/list";

        Map<String, Long[]> map= new HashMap<>();
        map.put("sku", skus.toArray(new Long[0]));

        HttpEntity<Map<String, Long[]>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_SkuProductsReport.class);
        } else {
            return null;
        }
    }

    //Загрузка и обновление товаров => Получить список товаров по идентификаторам
    @Nullable
    public OZON_SkuProductsReport getProductInfoByOfferId(String[] offerIds){
        String url = "https://api-seller.ozon.ru/v2/product/info/list";

        Map<String, String[]> map= new HashMap<>();
        map.put("offer_id", offerIds);

        HttpEntity<Map<String, String[]>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_SkuProductsReport.class);
        } else {
            return null;
        }
    }
}
