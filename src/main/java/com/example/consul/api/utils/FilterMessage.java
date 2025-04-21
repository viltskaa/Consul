package com.example.consul.api.utils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Data
public class FilterMessage {
    private Filter filter;
    private int page;
    private int page_size;

    public FilterMessage(Filter filter,int page,int page_size) {
        this.filter = filter;
        this.page=page;
        this.page_size=page_size;
    }

    @Getter
    @Setter
    @Data
    public static class Filter{
        private Date date;
        private ArrayList<String> operationType;
        private String transactionType; // тип начисления

        public Filter(String from, String to, ArrayList<String> operationType, String transactionType) {
            date = new Date(from, to);
            this.operationType = operationType;
            this.transactionType = transactionType;
        }
    }

    @Getter
    @Setter
    @Data
    static class Date {
        private String from;
        private String to;

        public Date(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }
}
