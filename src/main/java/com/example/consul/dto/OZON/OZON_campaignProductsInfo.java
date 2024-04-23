package com.example.consul.dto.OZON;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class OZON_campaignProductsInfo {
    private List<OZON_campaignProduct> products;

    public static class OZON_campaignProduct {
        private String sku;
        private String bid;
        private String title;
    }
}
