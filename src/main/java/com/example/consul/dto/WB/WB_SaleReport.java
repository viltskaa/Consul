package com.example.consul.dto.WB;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class WB_SaleReport {
    private String date;
    private String lastChangeDate;
    private String warehouseName;
    private String countryName;
    @SerializedName("oblastOkrugName")
    private String districtName;
    private String regionName;
    private String supplierArticle;
    private Integer nmId;
    private String barcode;
    private String category;
    private String subject;
    private String brand;
    private String techSize;
    private Integer incomeID;
    private Boolean isSupply;
    private Boolean isRealization;
    private Double totalPrice;
    private Integer discountPercent;
    private Double spp;
    private Double finishedPrice;
    private Double priceWithDisc;
    private Boolean isCancel;
    private String cancelDate;
    private String orderType;
    private String sticker;
    private String gNumber;
    private String srid;
}
