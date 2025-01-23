package com.example.consul.dto.WB;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// https://openapi.wildberries.ru/statistics/api/ru/#tag/Statistika/paths/~1api~1v3~1supplier~1reportDetailByPeriod/get
@Getter
@Setter
@Data
public class WB_DetailReport {
    @SerializedName("realizationreport_id")
    Integer realizationReportId;
    @SerializedName("date_from")
    String dateFrom;
    @SerializedName("date_to")
    String dateTo;
    @SerializedName("create_dt")
    String createDt;
    @SerializedName("currency_name")
    String currencyName;
    @SerializedName("suppliercontract_code")
    String supplierContractCode;
    @SerializedName("rrd_id")
    Long rrdId;
    @SerializedName("gi_id")
    Integer giId;
    @SerializedName("subject_name")
    String subjectName;
    @SerializedName("nm_id")
    Integer nmId;
    @SerializedName("brand_name")
    String brandName;
    @SerializedName("sa_name")
    String saName;
    @SerializedName("ts_name")
    String tsName;
    @SerializedName("barcode")
    String barcode;
    @SerializedName("doc_type_name")
    String docTypeName;
    @SerializedName("quantity")
    Integer quantity;
    @SerializedName("retail_price")
    Double retailPrice;
    @SerializedName("retail_amount")
    Double retailAmount;
    @SerializedName("sale_percent")
    Integer salePercent;
    @SerializedName("commission_percent")
    Double commissionPercent;
    @SerializedName("office_name")
    String officeName;
    @SerializedName("supplier_oper_name")
    String supplierOperName;
    @SerializedName("order_dt")
    String orderDt;
    @SerializedName("sale_dt")
    String saleDt;
    @SerializedName("rr_dt")
    String rrDt;
    @SerializedName("shk_id")
    Long shkId;
    @SerializedName("retail_price_withdisc_rub")
    Double retailPriceWithdiscRub;
    @SerializedName("delivery_amount")
    Integer deliveryAmount;
    @SerializedName("return_amount")
    Integer returnAmount;
    @SerializedName("delivery_rub")
    Double deliveryRub;
    @SerializedName("gi_box_type_name")
    String giBoxTypeName;
    @SerializedName("product_discount_for_report")
    Double productDiscountForReport;
    @SerializedName("supplier_promo")
    Double supplierPromo;
    @SerializedName("rid")
    Long rid;
    @SerializedName("ppvz_spp_prc")
    Double ppvzSppPrc;
    @SerializedName("ppvz_kvw_prc_base")
    Double ppvzKvwPrcBase;
    @SerializedName("ppvz_kvw_prc")
    Double ppvzKvwPrc;
    @SerializedName("sup_rating_prc_up")
    Double supRatingPrcUp;
    @SerializedName("is_kgvp_v2")
    Double isKgvpV2;
    @SerializedName("ppvz_sales_commission")
    Double ppvzSalesCommission;
    @SerializedName("ppvz_for_pay")
    Double ppvzForPay;
    @SerializedName("ppvz_reward")
    Double ppvzReward;
    @SerializedName("acquiring_fee")
    Double acquiringFee;
    @SerializedName("acquiring_bank")
    String acquiringBank;
    @SerializedName("ppvz_vw")
    Double ppvzVw;
    @SerializedName("ppvz_vw_nds")
    Double ppvzVwNds;
    @SerializedName("ppvz_office_id")
    Integer ppvzOfficeId;
    @SerializedName("ppvz_office_name")
    String ppvzOfficeName;
    @SerializedName("ppvz_supplier_id")
    Integer ppvzSupplierId;
    @SerializedName("ppvz_supplier_name")
    String ppvzSupplierName;
    @SerializedName("ppvz_inn")
    String ppvzInn;
    @SerializedName("declaration_number")
    String declarationNumber;
    @SerializedName("bonus_type_name")
    String bonusTypeName;
    @SerializedName("sticker_id")
    String stickerId;
    @SerializedName("site_country")
    String siteCountry;
    @SerializedName("penalty")
    Double penalty;
    @SerializedName("additional_payment")
    Double additionalPayment;
    @SerializedName("rebill_logistic_cost")
    Double rebillLogisticCost;
    @SerializedName("rebill_logistic_org")
    String rebillLogisticOrg;
    @SerializedName("kiz")
    String kiz;
    @SerializedName("storage_fee")
    Double storageFee;
    @SerializedName("deduction")
    Double deduction;
    @SerializedName("acceptance")
    Double acceptance;
    @SerializedName("srid")
    String srid;
    @SerializedName("report_type")
    Integer reportType;
}
