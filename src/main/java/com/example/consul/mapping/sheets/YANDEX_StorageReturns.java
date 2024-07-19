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
public class YANDEX_StorageReturns {
    private long businessAccountId;
    private String workModel;
    private long storeId;
    private String storeName;
    private String inn;
    private String placementContractNumber;
    private String promotionContractNumber;
    private String typeOfState;
    private long orderNumber;
    private long returnNumber;
    private int returnCount;
    private double tariffNonPurchase;
    private double tariffReturn;
    private LocalDateTime serviceDateTime;
    private LocalDate actFormationDate;
    private double serviceCost;
    private String type;
}
