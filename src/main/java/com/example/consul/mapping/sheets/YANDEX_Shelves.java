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
public class YANDEX_Shelves {
    private long businessAccountId;
    private String workModel;
    private long storeId;
    private String storeName;
    private String inn;
    private String placementContractNumber;
    private String promotionContractNumber;
    private long advertiserId;
    private long companyNumber;
    private String companyName;
    private String serviceType;
    private int quantityShows;
    private String budgetType;
    private double budgetVolume;
    private LocalDateTime serviceDateTime;
    private LocalDate actFormationDate;
    private Double bonus;
    private Double serviceCost;
}
