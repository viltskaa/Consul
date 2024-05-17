package com.example.consul.document.models;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.annotations.TotalCell;
import com.example.consul.document.configurations.ExcelCellType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TotalCell(
        formula = "(accrued-returnCost-showcasePlacing-deliveryToConsumer" +
                "-acceptAndTransferPayment-favorSorting-unredeemedStorage-adCampaignCost-loyaltyProgram-boostSales+promotionFavor)" +
                "/count"
)
public class YANDEX_TableRow {
    @CellUnit(name = "Артикул", total = false)
    private String offerId;
    @CellUnit(name = "Кол-во отгружено (шт)")
    private Double deliveryCount;
    @CellUnit(name = "Начислено")
    private Double accrued;
    @CellUnit(name = "Возврат (шт)")
    private Double returnCount;
    @CellUnit(name = "Стоимость возврата")
    private Double returnCost;
    @CellUnit(name = "Размещение товаров на витрине")
    private Double showcasePlacing;
    @CellUnit(name = "Доставка покупателю")
    private Double deliveryToConsumer;
    @CellUnit(name = "Приём и перевод платежа покупателя")
    private Double acceptAndTransferPayment;
    @CellUnit(name = "Услуги по обработке в сортировочном центре")
    private Double favorSorting;
    @CellUnit(name = "Хранение невыкуп. заказов/возвратов")
    private Double unredeemedStorage;
    @CellUnit(name = "Расходы на рекламные кампании")
    private Double adCampaignCost;
    @CellUnit(name = "Программа лояльности")
    private Double loyaltyProgram;
    @CellUnit(name = "Буст продаж")
    private Double boostSales;
    @CellUnit(name = "Услуга продвижения (плюс нам)")
    private Double promotionFavor;
    //кринж
    @CellUnit(name = "Кол-во отгружено - Возврат")
    private Double count;
    @CellUnit(name="Итого", type = ExcelCellType.TOTAL)
    private final Double total = 0.0;
}
