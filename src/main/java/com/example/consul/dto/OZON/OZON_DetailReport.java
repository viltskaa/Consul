package com.example.consul.dto.OZON;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class OZON_DetailReport {
    Result result;

    public OZON_DetailReport(Result result) {
        this.result = result;
    }

    @Data
    public static class Result{
        Header header;
        List<Row> rows;

        public Result(Header header, List<Row> rows) {
            this.header = header;
            this.rows = rows;
        }
    }

    @Data
    public static class Header{
        String doc_date;
        String number;
        String start_date;
        String stop_date;
        String contract_date;
        String contract_number;
        String payer_name;
        String payer_inn;
        String payer_kpp;
        String receiver_name;
        String receiver_inn;
        String receiver_kpp;
        Double doc_amount;
        Double vat_amount;
        String currency_sys_name;

        public Header(String doc_date, String num, String start_date, String stop_date, String contract_date, String contract_num, String payer_name, String payer_inn, String payer_kpp, String rcv_name, String rcv_inn, String rcv_kpp, Double doc_amount, Double vat_amount, String currency_code) {
            this.doc_date = doc_date;
            this.number = num;
            this.start_date = start_date;
            this.stop_date = stop_date;
            this.contract_date = contract_date;
            this.contract_number = contract_num;
            this.payer_name = payer_name;
            this.payer_inn = payer_inn;
            this.payer_kpp = payer_kpp;
            this.receiver_name = rcv_name;
            this.receiver_inn = rcv_inn;
            this.receiver_kpp = rcv_kpp;
            this.doc_amount = doc_amount;
            this.vat_amount = vat_amount;
            this.currency_sys_name = currency_code;
        }
    }

    @Data
    public static class DeliveryCommission{
        Double amount;
        Double bonus;
        Double commission;
        Double compensation;
        Double price_per_instance;
        Integer quantity;
        Double standard_fee;
        Double stars;
        Double total;
    }

    @Data
    public static class Item{
        String barcode;
        String name;
        String offer_id;
        Long sku;
    }

    @Data
    public static class ReturnCommission{
        Double amount;
        Double bonus;
        Double commission;
        Double compensation;
        Double price_per_instance;
        Integer quantity;
        Double standard_fee;
        Double stars;
        Double total;
    }

    @Data
    public static class Row{
        Double commission_ratio;
        DeliveryCommission delivery_commission;
        Item item;
        ReturnCommission return_commission;
        Integer rowNumber;
        Double seller_price_per_instance;

        public Row(Double commission_ratio, DeliveryCommission delivery_commission, Item item, ReturnCommission return_commission, Integer rowNumber, Double seller_price_per_instance) {
            this.commission_ratio = commission_ratio;
            this.delivery_commission = delivery_commission;
            this.item = item;
            this.return_commission = return_commission;
            this.rowNumber = rowNumber;
            this.seller_price_per_instance = seller_price_per_instance;
        }


        public static Row of(Row row) {
            return new Row(
                    row.getCommission_ratio(),
                    row.getDelivery_commission(),
                    row.getItem(),
                    row.getReturn_commission(),
                    row.getRowNumber(),
                    row.getSeller_price_per_instance());
        }
    }
}
