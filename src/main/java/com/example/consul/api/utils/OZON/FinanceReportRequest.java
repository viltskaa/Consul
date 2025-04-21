package com.example.consul.api.utils.OZON;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FinanceReportRequest {
    private int page;
    @SerializedName("page_size")
    private int pageSize;
    @SerializedName("with_details")
    private boolean withDetails;
    private Date date;

    public FinanceReportRequest(int page,
                                int pageSize,
                                boolean withDetails,
                                String from,
                                String to) {
        this.page = page;
        this.pageSize = pageSize;
        this.withDetails = withDetails;
        this.date = new Date(from, to);
    }

    @Data
    private static class Date {
        private String from;
        private String to;

        public Date(String from,
                    String to) {
            this.from = from;
            this.to = to;
        }
    }
}
