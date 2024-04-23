package com.example.consul.dto.OZON;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Отчет по трафаретам
 */
@Getter
@Setter
@Data
public class OZON_PerformanceReport {
    private String id;
    private String title;
    private Report report;

    @Getter
    @Setter
    @Data
    private static class Report {
        private List<Product> rows;
        private Totals totals;
    }

    @Getter
    @Setter
    @Data
    private static class Product {
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
    }

    @Getter
    @Setter
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

//    public static List<OZON_PerformanceReport> map(LinkedTreeMap<String,
//            LinkedTreeMap<String, LinkedTreeMap<String, String>>> tree) {
//        List<OZON_PerformanceReport> result = new ArrayList<>();
//        try {
//            for (String key : tree.keySet()) {
//                OZON_PerformanceReport report = new OZON_PerformanceReport();
//                List<Field> fields = Arrays.stream(report.getClass().getDeclaredFields()).toList();
//                fields.forEach(x -> x.setAccessible(true));
//                LinkedTreeMap<String, LinkedTreeMap<String, String>> obj = tree.get(key);
//
//                for (Field field : fields) {
//                    if (String.class.equals(field.getType())) {
//                        if (field.getName().equals("id")) {
//                            field.set(report, key);
//                        } else {
//                            field.set(report, obj.get(field.getName()));
//                        }
//                    } else {
//                        List<Field> inner_fields = Arrays.stream(field.getClass().getDeclaredFields()).toList();
//                        inner_fields.forEach(x -> x.setAccessible(true));
//                        for (Field f1 : inner_fields) {
//
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            return result;
//        }
//    }
}
