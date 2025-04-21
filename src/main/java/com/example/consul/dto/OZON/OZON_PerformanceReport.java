package com.example.consul.dto.OZON;

import lombok.Data;

import java.util.List;

/**
 * Отчет по трафаретам
 */
@Data
public class OZON_PerformanceReport {
    private String id;
    private String title;
    private Report report;

    @Data
    public static class Report {
        private List<Product> rows;
        private Totals totals;
    }

    @Data
    public static class Product {
        private String date;
        private String views;
        private String clicks;
        private String ctr;
        private String moneySpent;
        private String avgBid;
        private String orders;
        private String ordersMoney;
        private String models;
        private String modelsMoney;
        private String sku;
        private String price;
        private String title;
    }

    @Data
    private static class Totals {
        private String views;
        private String clicks;
        private String ctr;
        private String moneySpent;
        private String avgBid;
        private String orders;
        private String ordersMoney;
        private String models;
        private String modelsMoney;
        private String corrections;
    }
}
