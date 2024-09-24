package com.example.consul.dto.OZON;

import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * https://api-seller.ozon.ru/v2/product/info
 */
@Data
@Getter
public class OZON_SkuProductsReport {
    private Result result;

    @Data
    @Getter
    public static class Result {
        private List<OZON_SkuProduct> items;
    }

    @Data
    @Getter
    public static class OZON_SkuProduct {
        private Long id;//есть
        private String name;//есть
        private String offer_id;//есть
        private Boolean is_archived; //есть
        private Boolean is_autoarchived; //есть
        private Long sku; //есть
        private String barcode;//есть
        private List<String> barcodes; //есть
        private String buybox_price; //есть
        private Long category_id; //есть
        private Long description_category_id; //есть
        private Integer type_id; //есть
        private String created_at; //есть
        private List<String> images; //есть
        private String currency_code; //есть
        private String marketing_price;//есть
        private String min_ozon_price; //есть
        private String min_price; //есть
        private String old_price; //есть
        private String premium_price; //есть
        private String price; //есть
        private String recommended_price; //есть
        private List<Sources> sources; //есть
        private Boolean has_discounted_item; //есть
        private Boolean is_discounted; //есть
        private Stocks discounted_stocks; //есть
        private String state; //есть
        private Stocks stocks; //есть
        private List<String> errors; //есть
        private String updated_at; //есть
        private String vat; //есть
        private Boolean visible; //есть
        private Details visibility_details; //есть
        private String price_index; //есть
        private Object price_indexes; //есть
        private List<String> images360; //есть
        private Boolean is_kgt; //есть
        private String color_image; //есть
        private String primary_image; //есть
        private Status status; //есть
        private String service_type; //есть
        private Long fbo_sku; //есть
        private Long fbs_sku; //есть
        private String rating; //есть

        @Data
        private static class Sources{
            private Boolean is_enabled;
            private Long sku;
            private String source;
        } 

        @Data
        private static class Stocks {
            private Integer coming;
            private Integer present;
            private Integer reserved;
        }

        @Data
        private static class Details {
            private Boolean has_price;
            private Boolean has_stock;
            private Boolean active_product;
            private Object reasons;
        }

        @Data
        private static class Status {
            private String state;
            private String state_failed;
            private String moderate_status;
            private List<String> decline_reasons;
            private String validation_state;
            private String state_name;
            private String state_description;
            private Boolean is_failed;
            private Boolean is_created;
            private String state_tooltip;
            private List<Error> item_errors;
            private String state_updated_at;

            @Data
            private static class Error {
                private String code;
                private String state;
                private String level;
                private String description;
                private String field;
                private Integer attribute_id;
                private String attribute_name;
            }
        }
    }
}
