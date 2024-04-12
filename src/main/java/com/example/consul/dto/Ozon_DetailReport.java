package com.example.consul.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Ozon_DetailReport {
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
}
