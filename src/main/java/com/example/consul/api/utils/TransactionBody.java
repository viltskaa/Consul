package com.example.consul.api.utils;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class TransactionBody {
    private Filter filter;
    private int page;
    private int page_size;

    public TransactionBody(String from,
                           String to,
                           List<String> operationType,
                           String transactionType,
                           int page,
                           int page_size) {

        filter = new Filter(from, to, operationType, transactionType);
        this.page = page;
        this.page_size = page_size;
    }

    @Getter
    @Setter
    @Data
    private static class Filter {
        private Date date;
        private List<String> operation_type;
        private String posting_number = "";
        private String transaction_type; // тип начисления

        public Filter(String from,
                      String to,
                      List<String> operationType,
                      String transactionType) {
            date = new Date(from, to);
            this.operation_type = operationType;
            this.transaction_type = transactionType;
        }
    }

    @Getter
    @Setter
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
