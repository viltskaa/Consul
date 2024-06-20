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
public class YANDEX_AcceptingPayment {
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
    private double userPaid;
    private double tariff;
    private String tariffUnit;
    private LocalDateTime serviceDateTime;
    private LocalDate actFormationDate;
    private double serviceCost;
    private String type;
}
