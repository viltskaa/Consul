package com.example.consul;

import com.example.consul.document.models.WB_TableRow;
import com.example.consul.services.WB_Service;
import com.example.consul.utils.Clustering;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class SortingTest {
    @Autowired
    private Clustering clustering;
    @Autowired
    private WB_Service wbService;

    @Test
    void test() {
        List<WB_TableRow> data = wbService.getData(
                "eyJhbGciOiJFUzI1NiIsImtpZCI6IjIwMjQwMjI2djEiLCJ0eXAiOiJKV1QifQ.eyJlbnQiOjEsImV4cCI6MTczMDQxNjMyOSwiaWQiOiJkYzEwOTdkMS1jYTQ1LTRjZWMtYTQyOC0zNThiM2FhMDFiZjUiLCJpaWQiOjExOTExMzYzNiwib2lkIjo1OTU3MzQsInMiOjEwMjIsInNpZCI6IjdhZmRlMmI4LWM1ZGQtNGNmOC1iOTBmLTY3MGUxYzcxMmI5YSIsInQiOmZhbHNlLCJ1aWQiOjExOTExMzYzNn0.BIjm0DrZkyXtKu5_NdZ5fGoUQmhD6uzHnexGE1KtdMzznW6agpmUsiPRkh4I9xtxVBBRy6TSu_syn8Fj-jP-7g",
                2024, 4);

        Map<String, List<WB_TableRow>> value = clustering.of(data);

        value.entrySet().forEach(System.out::println);
    }
}
