package com.example.consul.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class Ozon_TransactionReport {
    Double amount;
    String operation_type_name;
    String type;
    Long sku;
    Double delivery_charge;
    Double return_delivery_charge;
    Double accruals_for_sale;
    Double sale_commission;
}
