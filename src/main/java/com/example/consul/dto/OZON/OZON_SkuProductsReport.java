package com.example.consul.dto.OZON;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * https://api-seller.ozon.ru/v2/product/info
 */
@Setter
@Getter
@Data
public class OZON_SkuProductsReport {
    private Result result;

    @Setter
    @Getter
    @Data
    private static class Result {
        private List<OZON_SkuProduct> items;
    }

    public Map<String, List<Long>> getSkuListByOfferId() {
        return result.getItems().stream()
                .collect(Collectors.toMap(OZON_SkuProduct::getOffer_id,
                        item -> item.getSources().stream()
                                .map(OZON_SkuProductsReport.OZON_SkuProduct.Sources::getSku)
                                .collect(Collectors.toList())));
    }

    public OZON_SkuProduct findBySku(Long sku) {
        return result.getItems().stream()
                .filter(x ->
                        x.getSources().stream().anyMatch(y -> y.sku.equals(sku))
                ).findFirst().orElse(null);
    }

    @Setter
    @Getter
    @Data
    public static class OZON_SkuProduct {
        private Long id;
        private String name;
        private String offer_id;
        private String barcode;
        private List<String> barcodes;
        private String buybox_price;
        private Long description_category_id;
        private Integer type_id;
        private String created_at;
        private List<String> images;
        private String currency_code;
        private String marketing_price;
        private String min_price;
        private String old_price;
        private String premium_price;
        private String price;
        private String recommended_price;
        private List<Sources> sources;
        private Boolean has_discounted_item;
        private Boolean is_discounted;
        private Stocks discounted_stocks;
        private String state;
        private Stocks stocks;
        private List<String> errors;
        private String updated_at;
        private String vat;
        private Boolean visible;
        private Details visibility_details;
        private Object price_indexes;
        private List<String> images360;
        private Boolean is_kgt;
        private String color_image;
        private String primary_image;
        private Status status;


        @Setter
        @Getter
        @Data
        private static class Sources{
            private Boolean is_enabled;
            private Long sku;
            private String source;
        }

        @Setter
        @Getter
        @Data
        private static class Stocks {
            private Integer coming;
            private Integer present;
            private Integer reserved;
        }

        @Setter
        @Getter
        @Data
        private static class Details {
            private Boolean has_price;
            private Boolean has_stock;
            private Boolean active_product;
            private Object reasons;
        }

        @Setter
        @Getter
        @Data
        private static class Status {
            private String status;
            private String state_failed;
            private String moderate_status;
            private List<String> decline_reasons;
            private String validation_state;
            private String state_name;
            private String state_description;
            private Boolean is_failed;
            private Boolean is_created;
            private String state_tooltip;
            private List<String> item_errors;
            private String state_updated_at;
        }
    }
}
