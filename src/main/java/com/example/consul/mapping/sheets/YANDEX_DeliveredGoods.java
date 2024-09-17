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
    private String productSku;
    private int quantityDelivered;
    private double totalDiscount;
    private double totalPriceWithDiscount;
}
