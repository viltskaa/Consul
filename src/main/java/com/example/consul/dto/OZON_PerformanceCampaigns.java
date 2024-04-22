package com.example.consul.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Data
public class OZON_PerformanceCampaigns {
    private List<OZON_PerformanceCampaign> rows;

    @Setter
    @Getter
    @Data
    public static class OZON_PerformanceCampaign {
        private String id;
        private String title;
        private String objectType;
        private String status;
        private String dailyBudget;
        private String budget;
        private String priority;
        private String moneySpent;
        private String views;
        private String clicks;
        private String avgBid;
        private String viewPrice;
        private String ctr;
        private String clickPrice;
        private String orders;
        private String ordersMoney;
        private String drr;
    }

}
