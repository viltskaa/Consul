package com.example.consul.dto.OZON;

import lombok.Data;

import java.util.List;

@Data
public class OZON_PerformanceCampaigns {
    private List<OZON_PerformanceCampaign> rows;

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
