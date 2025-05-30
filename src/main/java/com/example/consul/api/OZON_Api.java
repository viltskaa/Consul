package com.example.consul.api;


import com.example.consul.api.utils.OZON.FinanceReportRequest;
import com.example.consul.api.utils.TransactionBody;
import com.example.consul.dto.OZON.*;
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
     * Отчеты => Финансовый отчет
     *
     * @param from начало периода
     * @param to конец периода
     * @param withDetails получить отчет с детализацией
     * @param page номер страницы, возвращаемой в запросе
     * @param pageSize количество элементов на странице
     * @return Финансовый отчет в виде OZON_FinanceReport
     */
    @Nullable
    public OZON_FinanceReport getFinanceReport(@NotNull String from,
                                               @NotNull String to,
                                               @NotNull Boolean withDetails,
                                               @NotNull Integer page,
                                               @NotNull Integer pageSize) {
        final String financeReportUrl = "https://api-seller.ozon.ru/v1/finance/cash-flow-statement/list";

        HttpEntity<String> request = new HttpEntity<>(new Gson()
                .toJson(new FinanceReportRequest(page,
                                                 pageSize,
                                                 withDetails,
                                                 from,
                                                 to
                        )
                ),
                headers
        );

        ResponseEntity<String> response = restTemplate
                .postForEntity(financeReportUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_FinanceReport.class);
        } else {
            return null;
        }
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
                                                       @NotNull List<String> operation_type,
                                                       @NotNull String transaction_type,
                                                       @NotNull Integer page,
                                                       @NotNull Integer page_size) {
        final String transactionReportUrl = "https://api-seller.ozon.ru/v3/finance/transaction/list";

        HttpEntity<String> request = new HttpEntity<>(new Gson()
                .toJson(new TransactionBody(from,
                                            to,
                                            operation_type,
                                            transaction_type,
                                            page,
                                            page_size)
                        ),
                headers
        );

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
    public OZON_SkuProductsReport getProductInfoByOfferId(List<String> offerIds) {
        String url = "https://api-seller.ozon.ru/v2/product/info/list";

        Map<String, List<String>> map = new HashMap<>();
        map.put("offer_id", offerIds);

        HttpEntity<Map<String, List<String>>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_SkuProductsReport.class);
        } else {
            return null;
        }
    }

    /**
     * Загрузка и обновление товаров => Получить связанные SKU.
     * Метод для получения всех SKU по старым идентификаторам SKU
     *
     * @param skus идентификатор товара в системе Ozon
     * @return
     */
    @Nullable
    public OZON_RelatedSku getRelatedSku(List<Long> skus) {
        String url = "https://api-seller.ozon.ru/v1/product/related-sku/get";

        Map<String, List<Long>> map = new HashMap<>();
        map.put("sku", skus);

        HttpEntity<Map<String, List<Long>>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), OZON_RelatedSku.class);
        } else {
            return null;
        }
    }
}
