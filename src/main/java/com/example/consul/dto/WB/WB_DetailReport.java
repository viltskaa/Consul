package com.example.consul.dto.WB;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// https://openapi.wildberries.ru/statistics/api/ru/#tag/Statistika/paths/~1api~1v3~1supplier~1reportDetailByPeriod/get
@Getter
@Setter
@Data
public class WB_DetailReport {
    Integer realizationreport_id; //не надо
    String date_from; //не надо
    String date_to; //не надо
    String create_dt; //не надо
    String currency_name; //не надо
    String suppliercontract_code; //не надо
    Long rrd_id; //не надо
    Integer gi_id; //не надо
    String subject_name;
    Integer nm_id;
    String brand_name;
    String sa_name;
    String ts_name; //не надо
    String barcode; //не надо
    String doc_type_name;
    Integer quantity;
    Double retail_price;
    Double retail_amount;
    Integer sale_percent;
    Double commission_percent;
    String office_name; //не надо
    String supplier_oper_name;
    String order_dt; //не надо
    String sale_dt; //не надо
    String rr_dt; //не надо
    Long shk_id; //не надо
    Double retail_price_withdisc_rub;
    Integer delivery_amount;
    Integer return_amount;
    Double delivery_rub;
    String gi_box_type_name; //не надо
    Double product_discount_for_report;
    Double supplier_promo;
    Long rid; //не надо
    Double ppvz_spp_prc;
    Double ppvz_kvw_prc_base;
    Double ppvz_kvw_prc;
    Double sup_rating_prc_up;
    Double is_kgvp_v2;
    Double ppvz_sales_commission;
    Double ppvz_for_pay;
    Double ppvz_reward;
    Double acquiring_fee;
    String acquiring_bank; //не надо
    Double ppvz_vw;
    Double ppvz_vw_nds;
    Integer ppvz_office_id; //не надо
    String ppvz_office_name; //не надо
    Integer ppvz_supplier_id; //не надо
    String ppvz_supplier_name; //не надо
    String ppvz_inn; //не надо
    String declaration_number;
    String bonus_type_name;
    String sticker_id;
    String site_country; //не надо
    Double penalty;
    Double additional_payment;
    Double rebill_logistic_cost;
    String rebill_logistic_org; //не надо
    String kiz;
    Double storage_fee;
    Double deduction;
    Double acceptance;
    String srid; //не надо
    Integer report_type;
}
