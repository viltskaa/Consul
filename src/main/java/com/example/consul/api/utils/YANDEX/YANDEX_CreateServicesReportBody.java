package com.example.consul.api.utils.YANDEX;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Data
@Getter
@Setter
public class YANDEX_CreateServicesReportBody {
    private Long businessId;
    private String dateFrom;
    private String dateTo;
    private List<YANDEX_PlacementType> placementPrograms;

    public YANDEX_CreateServicesReportBody(Long businessId,
                                           String dateFrom,
                                           String dateTo,
                                           List<YANDEX_PlacementType> placementPrograms){
        this.businessId = businessId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.placementPrograms = placementPrograms;
    }
}
