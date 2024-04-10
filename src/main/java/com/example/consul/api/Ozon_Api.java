package com.example.consul.api;

import com.example.consul.api.utils.link;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

public class Ozon_Api {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    private final link transactionReportUrl = link.create(
            "https://api-seller.ozon.ru/v3/finance/transaction/list?");

    public Ozon_Api() {
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public void setApiKey(@NotNull String apiKey,@NotNull String clientId) {
        headers = new HttpHeaders();
        headers.add("Api-Key", apiKey);
        headers.add("Client-Id", clientId);
        headers.add("Content-Type", "application/json");
        headers.setContentType(MediaType.valueOf("text/csv; charset=UTF-8"));
    }
}
