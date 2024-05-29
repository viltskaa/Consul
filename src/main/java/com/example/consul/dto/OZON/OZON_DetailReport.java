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
        private Integer rowNumber;
        private Item item;
        @SerializedName("seller_price_per_instance")
        private Double sellerPricePerInstance;
        @SerializedName("delivery_commission")
        private DeliveryCommission deliveryCommission;
        @SerializedName("return_commission")
        private ReturnCommission returnCommission;
        @SerializedName("commission_ratio")
        private Double commissionRatio;
    }

    @Data
    public static class DeliveryCommission {
        private Double amount;
        private Double bonus;
        private Double commission;
        private Double compensation;
        private Double price_per_instance;
        private Integer quantity;
        private Double standard_fee;
        private Double stars;
        private Double total;
    }

    @Data
    public static class Item {
        private String barcode;
        private String name;
        private String offer_id;
        private Long sku;
    }

    @Data
    public static class ReturnCommission {
        private Double amount;
        private Double bonus;
        private Double commission;
        private Double compensation;
        private Double price_per_instance;
        private Integer quantity;
        private Double standard_fee;
        private Double stars;
        private Double total;
    }


}
