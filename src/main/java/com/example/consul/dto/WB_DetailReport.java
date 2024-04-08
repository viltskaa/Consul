package com.example.consul.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WB_DetailReport {
    Integer realizationreport_id;
    String date_from;
    String date_to;
    String create_dt;
    String currency_name;
    String suppliercontract_code;
    Integer rrd_id;
    Integer gi_id;
    String subject_name;
    Integer nm_id;
    String brand_name;
    String sa_name;
    String ts_name;
    String barcode;
    String doc_type_name;
    Integer quantity;
    Double retail_price;
    Double retail_amount;
    Integer sale_percent;
    Double commission_percent;
    String office_name;
    String supplier_oper_name;
    String order_dt;
    String sale_dt;
    String rr_dt;
    Integer shk_id;
    Double retail_price_withdisc_rub;
    Integer delivery_amount;
    Integer return_amount;
    Double delivery_rub;
    String gi_box_type_name;
    Double product_discount_for_report;
    Double supplier_promo;
    Integer rid;
    Double ppvz_spp_prc;
    Double ppvz_kvw_prc_base;
    Double ppvz_kvw_prc;
    Double sup_rating_prc_up;
    Double is_kgvp_v2;
    Double ppvz_sales_commission;
    Double ppvz_for_pay;
    Double ppvz_reward;
    Double acquiring_fee;
    String acquiring_bank;
    Double ppvz_vw;
    Double ppvz_vw_nds;
    Integer ppvz_office_id;
    String ppvz_office_name;
    Integer ppvz_supplier_id;
    String ppvz_supplier_name;
    String ppvz_inn;
    String declaration_number;
    String bonus_type_name;
    String sticker_id;
    String site_country;
    Double penalty;
    Double additional_payment;
    Double rebill_logistic_cost;
    String rebill_logistic_org;
    String kiz;
    Double storage_fee;
    Double deduction;
    Double acceptance;
    String srid;
    Integer report_type;
}
