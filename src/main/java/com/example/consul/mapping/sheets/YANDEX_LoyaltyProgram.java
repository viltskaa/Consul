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
public class YANDEX_LoyaltyProgram {
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
    private double pricePerUnit;
    private double userPaid;
    private int quantity;
    private String service;
    private long reviewId;
    private double tariffPerUnit;
    private String measurementUnit;
    private LocalDateTime serviceDateTime;
    private LocalDate actFormationDate;
    private double serviceCost;
}
