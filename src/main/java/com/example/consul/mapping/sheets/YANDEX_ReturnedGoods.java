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
public class YANDEX_ReturnedGoods {
    @ColumnName(name = ColumnNameEnum.SKU)
    private String productSku;
    @ColumnName(name = ColumnNameEnum.RETURN_COUNT)
    private int quantityReturned;
    @ColumnName(name = ColumnNameEnum.PRICE_WITH_DISCOUNT)
    private double priceWithDiscount;
}
