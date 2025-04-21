package com.example.consul.mapping.sheets;

import com.example.consul.mapping.annotations.ColumnName;
import com.example.consul.mapping.enums.ColumnNameEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class YANDEX_DeliveredGoods {
    @ColumnName(name = ColumnNameEnum.SKU)
    private String productSku;
    @ColumnName(name = ColumnNameEnum.DELIVERED_COUNT)
    private int quantityDelivered;
    @ColumnName(name = ColumnNameEnum.TOTAL_DISCOUNT)
    private double totalDiscount;
    @ColumnName(name = ColumnNameEnum.TOTAL_PRICE_WITH_DISCOUNT)
    private double totalPriceWithDiscount;
}
