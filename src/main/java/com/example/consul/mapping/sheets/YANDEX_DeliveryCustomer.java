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
public class YANDEX_DeliveryCustomer {
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
    private int quantity;
    private Double saleQuantum; // Квант продажи, возможно null
    private Double quantumInOrder; // Квантов в заказе, возможно null
    private Double pricePerQuantum; // Цена за квант, возможно null
    private Double weightKg; // Вес, кг, возможно null
    private Double volumeWeightKg; // Объемный вес, кг, возможно null
    private Double lengthCm; // Длина, см, возможно null
    private Double widthCm; // Ширина, см, возможно null
    private Double heightCm; // Высота, см, возможно null
    private Double sumOfDimensions; // Сумма трёх измерений, см, возможно null
    private Double localSalesShare; // Доля локальных продаж, %, возможно null
    private String service;
    private String fromLocation;
    private String toLocation;
    private Double tariffPerUnit;
    private String measurementUnit;
    private Double minTariffPerUnit; // Минимальный тариф за шт., возможно null
    private Double maxTariffPerUnit; // Максимальный тариф за шт., возможно null
    private Double serviceCostWithoutLimits; // Стоимость услуги без учёта ограничений тарифа, возможно null
    private Double localityCoefficient; // Коэффициент локальности, возможно null
    private LocalDateTime serviceDateTime;
    private LocalDate actFormationDate;
    private double serviceCost;
}
