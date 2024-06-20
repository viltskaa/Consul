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
public class YANDEX_ReturnedGoods {
    private long orderNumber;
    private String orderType;
    private String productName;
    private String warehouseSku;
    private String productSku;
    private int quantityDelivered;
    private int quantityReturned;
    private String orderStatus;
    private LocalDate orderDate;
    private LocalDate shipmentDate;
    private LocalDate deliveryDate;
    private LocalDate returnReceiptDate;
    private String paymentMethod;
    private String vatRate;
    private double priceWithoutDiscount;
    private double marketplaceDiscount;
    private double sberThankYouBonusDiscount;
    private double yandexPlusPointsDiscount;
    private double priceWithDiscount;
    private double totalReturnedPriceWithoutDiscount;
    private double totalDiscountForReturnedItems;
    private double totalReturnedPriceWithDiscount;
}
