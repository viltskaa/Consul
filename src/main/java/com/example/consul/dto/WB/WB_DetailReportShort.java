package com.example.consul.dto.WB;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WB_DetailReportShort {
    String subject_name;
    Integer nm_id;
    String brand_name;
    String sa_name;
    String doc_type_name;
    Integer quantity;
    Double retail_price;
    Double retail_amount;
    Integer sale_percent;
    Double commission_percent;
    String supplier_oper_name;
    Double retail_price_withdisc_rub;
    Integer delivery_amount;
    Integer return_amount;
    Double delivery_rub;
    Double product_discount_for_report;
    Double supplier_promo;
    Double ppvz_spp_prc;
    Double ppvz_kvw_prc_base;
    Double ppvz_kvw_prc;
    Double sup_rating_prc_up;
    Double is_kgvp_v2;
    Double ppvz_sales_commission;
    Double ppvz_for_pay;
    Double ppvz_reward;
    Double acquiring_fee;
    Double ppvz_vw;
    Double ppvz_vw_nds;
    String declaration_number;
    String bonus_type_name;
    String sticker_id;
    Double penalty;
    Double additional_payment;
    Double rebill_logistic_cost;
    String kiz;
    Double storage_fee;
    Double deduction;
    Double acceptance;
    Integer report_type;

    public static WB_DetailReportShort of(WB_DetailReport wb_detailReport) {
        return new WB_DetailReportShort(wb_detailReport.getSubject_name(),
                wb_detailReport.getNm_id(),
                wb_detailReport.getBrand_name(),
                wb_detailReport.getSa_name(),
                wb_detailReport.getDoc_type_name(),
                wb_detailReport.getQuantity(),
                wb_detailReport.getRetail_price(),
                wb_detailReport.getRetail_amount(),
                wb_detailReport.getSale_percent(),
                wb_detailReport.getCommission_percent(),
                wb_detailReport.getSupplier_oper_name(),
                wb_detailReport.getRetail_price_withdisc_rub(),
                wb_detailReport.getDelivery_amount(),
                wb_detailReport.getReturn_amount(),
                wb_detailReport.getDelivery_rub(),
                wb_detailReport.getProduct_discount_for_report(),
                wb_detailReport.getSupplier_promo(),
                wb_detailReport.getPpvz_spp_prc(),
                wb_detailReport.getPpvz_kvw_prc_base(),
                wb_detailReport.getPpvz_kvw_prc(),
                wb_detailReport.getSup_rating_prc_up(),
                wb_detailReport.getIs_kgvp_v2(),
                wb_detailReport.getPpvz_sales_commission(),
                wb_detailReport.getPpvz_for_pay(),
                wb_detailReport.getPpvz_reward(),
                wb_detailReport.getAcquiring_fee(),
                wb_detailReport.getPpvz_vw(),
                wb_detailReport.getPpvz_vw_nds(),
                wb_detailReport.getDeclaration_number(),
                wb_detailReport.getBonus_type_name(),
                wb_detailReport.getSticker_id(),
                wb_detailReport.getPenalty(),
                wb_detailReport.getAdditional_payment(),
                wb_detailReport.getRebill_logistic_cost(),
                wb_detailReport.getKiz(),
                wb_detailReport.getStorage_fee(),
                wb_detailReport.getDeduction(),
                wb_detailReport.getAcceptance(),
                wb_detailReport.getReport_type());
    }
}