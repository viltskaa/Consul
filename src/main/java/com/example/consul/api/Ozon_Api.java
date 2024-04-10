package com.example.consul.api;

import com.example.consul.dto.OZON_DetailReport;
import com.example.consul.dto.Ozon_TransactionReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Ozon_Api {
    private HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();

    private final String transactionReportUrl = "https://api-seller.ozon.ru/v3/finance/transaction/list";
    private final String detailReportUrl = "https://api-seller.ozon.ru/v1/finance/realization";

    public Ozon_Api() {
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public void setHeaders(@NotNull String apiKey,@NotNull String clientId) {
        headers = new HttpHeaders();
        headers.add("Api-Key", apiKey);
        headers.add("Client-Id", clientId);
        headers.add("Content-Type", "application/json");
        headers.setContentType(MediaType.valueOf("text/csv; charset=UTF-8"));
    }

    @Nullable
    public List<Ozon_TransactionReport> getTransactionReport(@NotNull String from,
                                                             @NotNull String to,
                                                             @NotNull String operation_type,
                                                             @NotNull String transaction_type){
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("from", from);
        map.add("to", to);
        MultiValueMap<String, MultiValueMap<String, String>> map2= new LinkedMultiValueMap<>();
        map2.add("date",map);
        MultiValueMap<String, MultiValueMap<String, MultiValueMap<String, String>>> map3= new LinkedMultiValueMap<>();
        map3.add("filter",map2);

        HttpEntity<MultiValueMap<String, MultiValueMap<String, MultiValueMap<String, String>>>> request = new HttpEntity<>(map3, headers);

        ResponseEntity<Ozon_TransactionReport[]> response = restTemplate
                .postForEntity(transactionReportUrl, request, Ozon_TransactionReport[].class );
        if (response.getStatusCode() == HttpStatus.OK) {
            return Arrays.asList(
                    Objects.requireNonNull(response.getBody())
            );
        } else {
            return null;
        }
    }

    @Nullable
    public List<OZON_DetailReport> getDetailReport(@NotNull String date){
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("date", date);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<OZON_DetailReport[]> response = restTemplate
                .postForEntity(detailReportUrl, request, OZON_DetailReport[].class );
        if (response.getStatusCode() == HttpStatus.OK) {
            return Arrays.asList(
                    Objects.requireNonNull(response.getBody())
            );
        } else {
            return null;
        }
    }
}
