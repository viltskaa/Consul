package com.example.consul.api.utils;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Data
public class ForTransactions {

    private Filter filter;
    private int page = 1;
    private int page_size = 1000;

    public ForTransactions(String from, String to, ArrayList<String> operationType, String transactionType){
        filter = new Filter(from, to, operationType, transactionType);
    }

    public ForTransactions(String from, String to, ArrayList<String> operationType, String transactionType,
                           int page,int page_size){

        filter = new Filter(from, to, operationType, transactionType);
        this.page=page;
        this.page_size=page_size;
    }

    @Getter
    @Setter
    @Data
    public class Filter {
        private Date date;
        private ArrayList<String> operation_type;
        private String posting_number = "";
        private String transaction_type; // тип начисления

        public Filter(String from, String to, ArrayList<String> operationType, String transactionType) {
            date = new Date(from, to);
            this.operation_type = operationType;
            this.transaction_type = transactionType;
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
