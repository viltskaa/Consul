package com.example.consul.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OZON_DetailReport {
    Integer row_number;
    Long product_id;
    String product_name;
    String barcode;
    String offer_id;
    Double commission_percent;
    Double price;
    Double price_sale;
    Double sale_amount;
    Double sale_commission;
    Double sale_discount;
    Double sale_price_seller;
    Integer sale_qty;
    Double return_sale;
    Double return_amount;
    Double return_commission;
    Double return_discount;
    Double return_price_seller;
    Integer return_qty;
}
