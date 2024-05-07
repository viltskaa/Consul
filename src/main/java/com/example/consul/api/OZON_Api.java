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

    public void setHeaders(@NotNull String apiKey, @NotNull String clientId) {
        headers = new HttpHeaders();
        headers.add("Api-Key", apiKey);
        headers.add("Client-Id", clientId);
        headers.add("Content-Type", "application/json");
    }


    /**
     * Финансовые отчеты => Список транзакций
     *
     * @param from начало периода
     * @param to конец периода
     * @param operation_type тип операции
     * @param transaction_type тип начисления
     * @param page номер страницы, возвращаемой в запросе
     * @param page_size количество элементов на странице
     * @return
     */
    @Nullable
    public OZON_TransactionReport getTransactionReport(@NotNull String from,
                                                       @NotNull String to,
                                                       @NotNull ArrayList<String> operation_type,
                                                       @NotNull String transaction_type,
                                                       @NotNull Integer page,
                                                       @NotNull Integer page_size) {
        final String transactionReportUrl = "https://api-seller.ozon.ru/v3/finance/transaction/list";

        HttpEntity<String> request = new HttpEntity<>
                (new Gson().toJson(new ForTransactions(from, to, operation_type, transaction_type, page, page_size)), headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(transactionReportUrl, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_TransactionReport.class);
        } else {
            return null;
        }
    }

    /**
     * Финансовые отчеты => Отчёт о реализации товаров
     *
     * @param month месяц для отчетного периода
     * @param year год для отчетного периода
     * @return
     */

    @Nullable
    public OZON_DetailReport getDetailReport(@NotNull Integer month,
                                             @NotNull Integer year) {
        String detailReportUrl = "https://api-seller.ozon.ru/v2/finance/realization";

        Map<String, Integer> map = new HashMap<>();
        map.put("month", month);
        map.put("year", year);

        HttpEntity<Map<String, Integer>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(detailReportUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_DetailReport.class);
        } else {
            return null;
        }
    }

    /**
     * Загрузка и обновление товаров => Получить список товаров по идентификаторам.
     * Получение списка товаров по идентификатору в системе OZON - SKU
     *
     * @param skus идентификатор товара в системе Ozon
     * @return
     */
    @Nullable
    public OZON_SkuProductsReport getProductInfo(List<Long> skus) {
        String url = "https://api-seller.ozon.ru/v2/product/info/list";

        Map<String, Long[]> map = new HashMap<>();
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

    /**
     * Загрузка и обновление товаров => Получить список товаров по идентификаторам.
     * Получение списка товаров по идентификатору в системе продавца - артикул
     *
     * @param offerIds идентификатор товара в системе продавца
     * @return
     */
    @Nullable
    public OZON_SkuProductsReport getProductInfoByOfferId(String[] offerIds) {
        String url = "https://api-seller.ozon.ru/v2/product/info/list";

        Map<String, String[]> map = new HashMap<>();
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
