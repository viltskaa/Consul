package com.example.consul.dto.OZON;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class OZON_DetailReport {
    private Result result;

    @Data
    public static class Result {
        private Header header;
        private List<Row> rows;
    }

    @Data
    private static class Header {
        private String doc_date;
        private String number;
        private String start_date;
        private String stop_date;
        private String contract_date;
        private String contract_number;
        private String payer_name;
        private String payer_inn;
        private String payer_kpp;
        private String receiver_name;
        private String receiver_inn;
        private String receiver_kpp;
        private Double doc_amount;
        private Double vat_amount;
        private String currency_sys_name;
    }

    @Data
    @AllArgsConstructor
    public static class Row {
        @SerializedName("commission_ratio")
        private Double commissionRatio;
        @SerializedName("delivery_commission")
        private DeliveryCommission deliveryCommission;
        private Item item;
        @SerializedName("return_commission")
        private ReturnCommission returnCommission;
        private Integer rowNumber;
        @SerializedName("seller_price_per_instance")
        private Double sellerPricePerInstance;
    }

    @Data
    public static class DeliveryCommission {
        private Double amount;
        private Double bonus;
        private Double commission;
        private Double compensation;
        @SerializedName("price_per_instance")
        private Double pricePerInstance;
        private Integer quantity;
        @SerializedName("standard_fee")
        private Double standardFee;
        private Double stars;
        private Double total;
    }

    @Data
    public static class Item {
        private String barcode;
        private String name;
        @SerializedName("offer_id")
        private String offerId;
        private Long sku;
    }

    @Data
    public static class ReturnCommission {
        private Double amount;
        private Double bonus;
        private Double commission;
        private Double compensation;
        @SerializedName("price_per_instance")
        private Double pricePerInstance;
        private Integer quantity;
        @SerializedName("standard_fee")
        private Double standardFee;
        private Double stars;
        private Double total;
    }
}
