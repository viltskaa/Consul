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
        String num;
        String start_date;
        String stop_date;
        String contract_date;
        String contract_num;
        String payer_name;
        String payer_inn;
        String payer_kpp;
        String rcv_name;
        String rcv_inn;
        String rcv_kpp;
        Double doc_amount;
        Double vat_amount;
        String currency_code;

        public Header(String doc_date, String num, String start_date, String stop_date, String contract_date, String contract_num, String payer_name, String payer_inn, String payer_kpp, String rcv_name, String rcv_inn, String rcv_kpp, Double doc_amount, Double vat_amount, String currency_code) {
            this.doc_date = doc_date;
            this.num = num;
            this.start_date = start_date;
            this.stop_date = stop_date;
            this.contract_date = contract_date;
            this.contract_num = contract_num;
            this.payer_name = payer_name;
            this.payer_inn = payer_inn;
            this.payer_kpp = payer_kpp;
            this.rcv_name = rcv_name;
            this.rcv_inn = rcv_inn;
            this.rcv_kpp = rcv_kpp;
            this.doc_amount = doc_amount;
            this.vat_amount = vat_amount;
            this.currency_code = currency_code;
        }
    }

    @Data
    public static class Row{
        Integer row_number; // Номер строки в отчёте
        Long product_id; // Идентификатор товара
        String product_name; // Наименование товара
        String barcode; // Штрихкод товара
        String offer_id; // Код товара продавца — артикул
        Double commission_percent; // Комиссия за продажу по категории
        Double price; // Цена продавца с учётом его скидки
        Double price_sale; // Цена реализации — цена, по которой покупатель приобрёл товар (для реализованных товаров)
        Double sale_amount; // Реализовано на сумму
        Double sale_commission; // Комиссия за реализованный товар с учётом скидок и наценки
        Double sale_discount; // Сумма, которую Ozon компенсирует продавцу, если скидка Ozon больше или равна комиссии за продажу
        Double sale_price_seller; // Итого к начислению за реализованный товар
        Integer sale_qty; // Количество реализованного товара
        Double return_sale; // Цена реализации — цена, по которой покупатель приобрёл товар (для возвращённых товаров)
        Double return_amount; // Возвращено на сумму
        Double return_commission; // sale_commission Ozon компенсирует её в случае возврата товара
        Double return_discount; // Сумма скидки за счёт Ozon по возвращённому товару
        Double return_price_seller; // Итого возвращено
        Integer return_qty; // Количество возвращённого товара

        public Row(Integer row_number, Long product_id, String product_name, String barcode, String offer_id, Double commission_percent, Double price, Double price_sale, Double sale_amount, Double sale_commission, Double sale_discount, Double sale_price_seller, Integer sale_qty, Double return_sale, Double return_amount, Double return_commission, Double return_discount, Double return_price_seller, Integer return_qty) {
            this.row_number = row_number;
            this.product_id = product_id;
            this.product_name = product_name;
            this.barcode = barcode;
            this.offer_id = offer_id;
            this.commission_percent = commission_percent;
            this.price = price;
            this.price_sale = price_sale;
            this.sale_amount = sale_amount;
            this.sale_commission = sale_commission;
            this.sale_discount = sale_discount;
            this.sale_price_seller = sale_price_seller;
            this.sale_qty = sale_qty;
            this.return_sale = return_sale;
            this.return_amount = return_amount;
            this.return_commission = return_commission;
            this.return_discount = return_discount;
            this.return_price_seller = return_price_seller;
            this.return_qty = return_qty;
        }

        public static Row of(Row row) {
            return new Row(row.getRow_number(),
                    row.getProduct_id(),
                    row.getProduct_name(),
                    row.getBarcode(),
                    row.getOffer_id(),
                    row.getCommission_percent(),
                    row.getPrice(),
                    row.getPrice_sale(),
                    row.getSale_amount(),
                    row.getSale_commission(),
                    row.getSale_discount(),
                    row.getSale_price_seller(),
                    row.getSale_qty(),
                    row.getReturn_sale(),
                    row.getReturn_amount(),
                    row.getReturn_commission(),
                    row.getReturn_discount(),
                    row.getReturn_price_seller(),
                    row.getReturn_qty());
        }
    }
}
