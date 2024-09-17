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
    private String productSku;
    private int quantityReturned;
    private double priceWithDiscount;
}
