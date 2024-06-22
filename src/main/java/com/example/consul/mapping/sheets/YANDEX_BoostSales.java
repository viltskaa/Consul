package com.example.consul.mapping.sheets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class YANDEX_BoostSales {
    private long businessAccountId;
    private String workModel;
    private long storeId;
    private String storeName;
    private String inn;
    private String placementContractNumber;
    private String promotionContractNumber;
    private long orderNumber;
    private String sku;
    private String productName;
    private String category;
    private double pricePerUnit;
    private int quantity;
    private String service;
    private double ratePercentage;
    private Double prepayment;
    private Double postpayment;
    private Double paymentBonuses;
    private LocalDate actFormationDate;
    private LocalDateTime serviceDateTime;
}
