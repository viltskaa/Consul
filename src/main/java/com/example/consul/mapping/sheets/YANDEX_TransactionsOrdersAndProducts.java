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
public class YANDEX_TransactionsOrdersAndProducts {
    private long businessAccountId;
    private String workModel;
    private long storeId;
    private String storeName;
    private String inn;
    private String placementContractNumber;
    private String promotionContractNumber;
    private long orderNumber;
    private String storeOrderNumber;
    private LocalDate registrationDate;
    private String orderType;
    private String sku;
}
