package com.example.consul.dto.WB;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// https://openapi.wildberries.ru/statistics/api/ru/#tag/Statistika/paths/~1api~1v3~1supplier~1reportDetailByPeriod/get
@Getter
@Setter
@Data
public class WB_DetailReport {
    @JsonProperty("realizationreport_id")
    Integer realizationreportId;
    @JsonProperty("date_from")
    String dateFrom;
    @JsonProperty("date_to")
    String dateTo;
    @JsonProperty("create_dt")
    String createDt;
    @JsonProperty("currency_name")
    String currencyName;
    @JsonProperty("suppliercontract_code")
    String suppliercontractCode;
    @JsonProperty("rrd_id")
    Long rrdId;
    @JsonProperty("gi_id")
    Integer giId;
    @JsonProperty("dlv_prc")
    Double dlvPrc;
    @JsonProperty("fix_tariff_date_from")
    String fixTariffDateFrom;
    @JsonProperty("fix_tariff_date_to")
    String fixTariffDateTo;
    @JsonProperty("subject_name")
    String subjectName;
    @JsonProperty("nm_id")
    Integer nmId;
    @JsonProperty("brand_name")
    String brandName;
    @JsonProperty("sa_name")
    String saName;
    @JsonProperty("ts_name")
    String tsName;
    @JsonProperty("barcode")
    String barcode;
    @JsonProperty("doc_type_name")
    String docTypeName;
    @JsonProperty("quantity")
    Integer quantity;
    @JsonProperty("retail_price")
    Double retailPrice;
    @JsonProperty("retail_amount")
    Double retailAmount;
    @JsonProperty("sale_percent")
    Integer salePercent;
    @JsonProperty("commission_percent")
    Double commissionPercent;
    @JsonProperty("office_name")
    String officeName;
    @JsonProperty("supplier_oper_name")
    String supplierOperName;
    @JsonProperty("order_dt")
    String orderDt;
    @JsonProperty("sale_dt")
    String saleDt;
    @JsonProperty("rr_dt")
    String rrDt;
    @JsonProperty("shk_id")
    Long shkId;
    @JsonProperty("retail_price_withdisc_rub")
    Double retailPriceWithdiscRub;
    @JsonProperty("delivery_amount")
    Integer deliveryAmount;
    @JsonProperty("return_amount")
    Integer returnAmount;
    @JsonProperty("delivery_rub")
    Double deliveryRub;
    @JsonProperty("gi_box_type_name")
    String giBoxTypeName;
    @JsonProperty("product_discount_for_report")
    Double productDiscountForReport;
    @JsonProperty("supplier_promo")
    Double supplierPromo;
    @JsonProperty("rid")
    Long rid;
    @JsonProperty("ppvz_spp_prc")
    Double ppvzSppPrc;
    @JsonProperty("ppvz_kvw_prc_base")
    Double ppvzKvwPrcBase;
    @JsonProperty("ppvz_kvw_prc")
    Double ppvzKvwPrc;
    @JsonProperty("sup_rating_prc_up")
    Double supRatingPrcUp;
    @JsonProperty("is_kgvp_v2")
    Double isKgvpV2;
    @JsonProperty("ppvz_sales_commission")
    Double ppvzSalesCommission;
    @JsonProperty("ppvz_for_pay")
    Double ppvzForPay;
    @JsonProperty("ppvz_reward")
    Double ppvzReward;
    @JsonProperty("acquiring_fee")
    Double acquiringFee;
    @JsonProperty("acquiring_percent")
    Double acquiringPercent;
    @JsonProperty("payment_processing")
    String paymentProcessing;
    @JsonProperty("acquiring_bank")
    String acquiringBank;
    @JsonProperty("ppvz_vw")
    Double ppvzVw;
    @JsonProperty("ppvz_vw_nds")
    Double ppvzVwNds;
    @JsonProperty("ppvz_office_id")
    Integer ppvzOfficeId;
    @JsonProperty("ppvz_office_name")
    String ppvzOfficeName;
    @JsonProperty("ppvz_supplier_id")
    Integer ppvzSupplierId;
    @JsonProperty("ppvz_supplier_name")
    String ppvzSupplierName;
    @JsonProperty("ppvz_inn")
    String ppvzInn;
    @JsonProperty("declaration_number")
    String declarationNumber;
    @JsonProperty("bonus_type_name")
    String bonusTypeName;
    @JsonProperty("sticker_id")
    String stickerId;
    @JsonProperty("site_country")
    String siteCountry;
    @JsonProperty("srv_dbs")
    Boolean srvDbs;
    @JsonProperty("penalty")
    Double penalty;
    @JsonProperty("additional_payment")
    Double additionalPayment;
    @JsonProperty("rebill_logistic_cost")
    Double rebillLogisticCost;
    @JsonProperty("kiz")
    String kiz;
    @JsonProperty("storage_fee")
    Double storageFee;
    @JsonProperty("deduction")
    Double deduction;
    @JsonProperty("acceptance")
    Double acceptance;
    @JsonProperty("assembly_id")
    Long assemblyId;
    @JsonProperty("srid")
    String srid;
    @JsonProperty("report_type")
    Integer reportType;
    @JsonProperty("is_legal_entity")
    Boolean isLegalEntity;
    @JsonProperty("trbx_id")
    String trbxId;
}
