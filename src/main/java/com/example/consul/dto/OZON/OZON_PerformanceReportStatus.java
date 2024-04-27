package com.example.consul.dto.OZON;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Data
public class OZON_PerformanceReportStatus {
    private String UUID;
    private State state;
    private String createdAt;
    private String updatedAt;
    private Request request;
    private String error;
    private String link;
    private String kind;

    @Setter
    @Getter
    @Data
    private static class Request {
        private String attributionDays;
        private List<Integer> campaigns;
        private String dateFrom;
        private String dateTo;
        private String from;
        private String groupBy;
        private List<String> objects;
        private String to;
    }

    public enum State {
        NOT_STARTED,
        IN_PROGRESS,
        ERROR,
        OK
    }
}
