package com.example.consul.dto.OZON;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class OZON_RelatedSku {
    private List<Item> items;
    private List<Error> errors;

    @Data
    @Getter
    public static class Item {
        @SerializedName("delivery_schema")
        private String deliverySchema;
        private String availability;
        @SerializedName("deleted_at")
        private String deletedAt;
        @SerializedName("product_id")
        private Long productId;
        private Long sku;
    }

    @Data
    public static class Error {
        private String code;
        private String message;
        private Long sku;
    }
}
