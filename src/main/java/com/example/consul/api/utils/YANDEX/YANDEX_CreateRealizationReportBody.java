package com.example.consul.api.utils.YANDEX;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class YANDEX_CreateRealizationReportBody {
    private Long campaignId;
    private int year;
    private int month;

    public YANDEX_CreateRealizationReportBody(Long campaignId, int year, int month){
        this.campaignId = campaignId;
        this.year = year;
        this.month = month;
    }
}
