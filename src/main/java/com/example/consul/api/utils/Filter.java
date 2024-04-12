package com.example.consul.api.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Filter {
    private Date date;
    private ArrayList<String> operationType;
    private String transactionType; // тип начисления

    public Filter(String from, String to, ArrayList<String> operationType, String transactionType) {
        date = new Date(from, to);
        this.operationType = operationType;
        this.transactionType = transactionType;
    }

    class Date {
        private String from;
        private String to;

        public Date(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }
}
