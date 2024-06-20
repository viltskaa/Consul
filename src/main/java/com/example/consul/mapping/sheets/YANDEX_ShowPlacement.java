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
public class YANDEX_ShowPlacement {
    private long businessAccountId;
    private String workModel;
    private long storeId;
    private String storeName;
    private String inn;
    private String placementContractNumber;
    private String promotionContractNumber;
    private long orderNumber;
    private LocalDateTime orderCreationDate;
    private String sku;
    private String productName;
    private double pricePerUnit;
    private double priceDifference;
    private int quantity;
    private int salesQuantum;
    private int quantumsInOrder;
    private double pricePerQuantum;
    private double weightKg;
    private double lengthCm;
    private double widthCm;
    private double heightCm;
    private double sumOfDimensions;
    private String paymentMethod;
    private String qualityIndex;
    private String service;
    private String tariffCondition;
    private double tariffPerUnit;
    private String measurementUnit;
    private double minTariffPerUnit;
    private double maxTariffPerUnit;
    private double serviceCostBeforeMinTariff;
    private LocalDateTime serviceDateTime;
    private LocalDate actFormationDate;
    private double serviceCostWithoutDiscounts;
    private double tariffPercent;
    private double discount;
    private double lateDeliveryPenaltyPercent;
    private double sellerFaultPenaltyPercent;
    private double minCostPerUnit;
    private double maxCostPerUnit;
    private double serviceCostChange1;
    private double tariff;
    private double serviceCostChange2;
    private double individualServiceDiscount;
    private double loyaltyDiscount;
    private double serviceCost;
}
