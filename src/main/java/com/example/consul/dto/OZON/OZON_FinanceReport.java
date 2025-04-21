package com.example.consul.dto.OZON;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class OZON_FinanceReport {
    private Result result;

    @Data
    public static class Result {
        @SerializedName("cash_flows")
        private List<CashFlow> cashFlows;
        private List<Details> details;
        @SerializedName("page_count")
        private Long pageCount;
    }

    @Data
    public static class Details {
        @SerializedName("begin_balance_amount")
        private Double beginBalanceAmount;
        private Delivery delivery;
        @SerializedName("invoice_transfer")
        private Double invoiceTransfer;
        private Double loan;
        private List<Payments> payments;
        private Period period;
        @SerializedName("return")
        private Return returnData;
        private RFBS rfbs;
        private Services services;
        private Others others;
        @SerializedName("end_balance_amount")
        private Double endBalanceAmount;
    }

    @Data
    public static class Others {
        private Double total;
        private List<Items> items;
    }

    @Data
    public static class Services {
        private Double total;
        private List<Items> items;
    }

    @Data
    private static class RFBS {
        private Double total;
        @SerializedName("transfer_delivery")
        private Double transferDelivery;
        @SerializedName("transfer_delivery_return")
        private Double transferDeliveryReturn;
        @SerializedName("compensation_delivery_return")
        private Double compensationDeliveryReturn;
        @SerializedName("partial_compensation")
        private Double partialCompensation;
        @SerializedName("partial_compensation_return")
        private Double partialCompensationReturn;
    }

    @Data
    private static class Return {
        private Double total;
        private Double amount;
        @SerializedName("return_services")
        private ReturnServices returnServices;
    }

    @Data
    private static class ReturnServices {
        private Double total;
        private List<Items> items;
    }

    @Data
    private static class Payments {
        @SerializedName("currency_code")
        private String currencyCode;
        private Double payment;
    }

    @Data
    private static class Delivery {
        private Double total;
        private Double amount;
        @SerializedName("delivery_services")
        private DeliveryServices deliveryServices;
    }

    @Data
    private static class DeliveryServices {
        private Double total;
        private List<Items> items;
    }

    @Data
    public static class Items {
        private String name;
        private Double price;
    }

    @Data
    private static class CashFlow {
        @SerializedName("orders_amount")
        private Double ordersAmount;
        @SerializedName("returns_amount")
        private Double returnsAmount;
        @SerializedName("commission_amount")
        private Double commissionAmount;
        @SerializedName("services_amount")
        private Double servicesAmount;
        @SerializedName("item_delivery_and_return_amount")
        private Double itemDeliveryAndReturnAmount;
        @SerializedName("currency_code")
        private String currencyCode;
        private Period period;
    }

    @Data
    private static class Period {
        private String begin;
        private String end;
        private Long id;
    }
}
