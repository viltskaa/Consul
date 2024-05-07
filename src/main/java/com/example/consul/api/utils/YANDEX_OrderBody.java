package com.example.consul.api.utils;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Data
public class YANDEX_OrderBody {
    private Long businessId;
    private String dateFrom;
    private String dateTo;
    private ArrayList<Long> campaignIds;

    public void YANDEX_OrderBody(Long businessId,
                                 String dateFrom,
                                 String dateTo,
                                 ArrayList<Long> campaignIds)
    {
        this.businessId = businessId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.campaignIds = campaignIds;
    }
}
