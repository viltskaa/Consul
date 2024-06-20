package com.example.consul.mapping.sheets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class YANDEX_DeliveredGoods {
    private long orderNumber;
    private String orderType;
    private String productName;
    private String productSku;
    private String warehouseSku;
    private int quantityShipped;
    private int quantityDelivered;
    private String orderStatus;
    private LocalDate orderDate;
    private LocalDate shipmentDate;
    private LocalDate deliveryDate;
    private String paymentMethod;
    private String vatRate;
    private double priceWithoutDiscount;
    private double marketplaceDiscount;
    private double sberThankYouBonusDiscount;
    private double yandexPlusPointsDiscount;
    private double priceWithDiscount;
    private double totalPriceWithoutDiscount;
    private double totalDiscount;
    private double totalPriceWithDiscount;
}
