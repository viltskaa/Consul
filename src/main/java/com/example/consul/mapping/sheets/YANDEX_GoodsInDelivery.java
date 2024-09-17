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
public class YANDEX_GoodsInDelivery {
    @ColumnName(name = ColumnNameEnum.ORDER_NUMBER)
    private long orderNumber;
    @ColumnName(name = ColumnNameEnum.SKU)
    private String productSku;
}
