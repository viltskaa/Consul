package com.example.consul.api;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Component
public class YANDEX_Api {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    public YANDEX_Api(){
                restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public void setHeaders(@NotNull String apiKey, @NotNull String clientId) {
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer y0_AgAAAABzBvISAAu7EwAAAAED4UtWAAAtEQmj-qVJyrHP6B9zqdC6RMWeeA");
        headers.add("Content-Type", "application/json");
    }

    public void getOrdersReport(@NotNull Long businessId,
                                @NotNull String dateFrom,
                                @NotNull String dateTo)
    {
        final String ordersReport = "https://api.partner.market.yandex.ru/reports/united-orders/generate?format=FILE&language=RU";

    }
}
