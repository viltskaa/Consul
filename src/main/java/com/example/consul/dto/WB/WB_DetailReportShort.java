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
        return new WB_DetailReportShort(wb_detailReport.getSubjectName(),
                wb_detailReport.getNmId(),
                wb_detailReport.getBrandName(),
                wb_detailReport.getSaName(),
                wb_detailReport.getDocTypeName(),
                wb_detailReport.getQuantity(),
                wb_detailReport.getRetailPrice(),
                wb_detailReport.getRetailAmount(),
                wb_detailReport.getSalePercent(),
                wb_detailReport.getCommissionPercent(),
                wb_detailReport.getSupplierOperName(),
                wb_detailReport.getRetailPriceWithdiscRub(),
                wb_detailReport.getDeliveryAmount(),
                wb_detailReport.getReturnAmount(),
                wb_detailReport.getDeliveryRub(),
                wb_detailReport.getProductDiscountForReport(),
                wb_detailReport.getSupplierPromo(),
                wb_detailReport.getPpvzSppPrc(),
                wb_detailReport.getPpvzKvwPrcBase(),
                wb_detailReport.getPpvzKvwPrc(),
                wb_detailReport.getSupRatingPrcUp(),
                wb_detailReport.getIsKgvpV2(),
                wb_detailReport.getPpvzSalesCommission(),
                wb_detailReport.getPpvzForPay(),
                wb_detailReport.getPpvzReward(),
                wb_detailReport.getAcquiringFee(),
                wb_detailReport.getPpvzVw(),
                wb_detailReport.getPpvzVwNds(),
                wb_detailReport.getDeclarationNumber(),
                wb_detailReport.getBonusTypeName(),
                wb_detailReport.getStickerId(),
                wb_detailReport.getPenalty(),
                wb_detailReport.getAdditionalPayment(),
                wb_detailReport.getRebillLogisticCost(),
                wb_detailReport.getKiz(),
                wb_detailReport.getStorageFee(),
                wb_detailReport.getDeduction(),
                wb_detailReport.getAcceptance(),
                wb_detailReport.getReportType());
    }
}