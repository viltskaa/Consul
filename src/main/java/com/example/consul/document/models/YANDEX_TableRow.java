package com.example.consul.document.models;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.annotations.TotalCell;
import com.example.consul.document.configurations.ExcelCellType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TotalCell(
        formula = "(accrued-returnCost-showcasePlacing-deliveryToConsumer" +
                "-acceptAndTransferPayment-favorSorting-unredeemedStorage-adCampaignCost-loyaltyProgram-boostSales-shelves)" +
                "/deliveryCount-returnCount"
)
public class YANDEX_TableRow extends TableRow {
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
    @CellUnit(name = "Полки")
    private Double shelves;
//    @CellUnit(name = "Услуга продвижения (плюс нам)")
//    private Double promotionFavor;
    @CellUnit(name="Итого", type = ExcelCellType.TOTAL)
    private final Double total = 0.0;

    @Builder
    public YANDEX_TableRow(String article,
                           Double deliveryCount,
                           Double accrued,
                           Double returnCount,
                           Double returnCost,
                           Double showcasePlacing,
                           Double deliveryToConsumer,
                           Double acceptAndTransferPayment,
                           Double favorSorting,
                           Double unredeemedStorage,
                           Double adCampaignCost,
                           Double loyaltyProgram,
                           Double boostSales,
                           Double shelves) {
        super(article);
        this.deliveryCount = deliveryCount;
        this.accrued = accrued;
        this.returnCount = returnCount;
        this.returnCost = returnCost;
        this.showcasePlacing = showcasePlacing;
        this.deliveryToConsumer = deliveryToConsumer;
        this.acceptAndTransferPayment = acceptAndTransferPayment;
        this.favorSorting = favorSorting;
        this.unredeemedStorage = unredeemedStorage;
        this.adCampaignCost = adCampaignCost;
        this.loyaltyProgram = loyaltyProgram;
        this.boostSales = boostSales;
        this.shelves = shelves;
//        this.promotionFavor = promotionFavor;
    }
}
