package com.example.consul.document.models;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.annotations.TotalCell;
import com.example.consul.document.v1.configurations.ExcelCellType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@TotalCell(
        formula = "(saleForDelivered-sumReturn-salesCommission-shipmentProcessing" +
                "-logistic-lastMile-acquiring-installment-returnProcessing-returnDelivery" +
                "-ozonPremium-searchPromotion-stencilProduct-cashbackIndividualPoints+compensation" +
                "-crossDockingDelivery-other-buyReview-disposal)/IF(delivered-returned=0,1,delivered-returned)-2.47"
)
public class OZON_TableRow extends TableRow {
    @CellUnit(name = "Доставлено")
    private Integer delivered;
    @CellUnit(name = "Возвращено")
    private Integer returned;
    @CellUnit(name = "Начислено за доставленный товар")
    private Double saleForDelivered;
    @CellUnit(name = "Возврат", type = ExcelCellType.EXPENSIVE)
    private Double sumReturn;
    @CellUnit(name = "Комиссия за продажу", type = ExcelCellType.EXPENSIVE)
    private Double salesCommission;
    @CellUnit(name = "Обработка отправлений")
    private Double shipmentProcessing;
    @CellUnit(name = "Логистика до покупателя")
    private Double logistic;
    @CellUnit(name = "Последняя миля")
    private Double lastMile;
    @CellUnit(name = "Эквайринг")
    private Double acquiring;
    @CellUnit(name = "Ozon рассрочка")
    private Double installment;
    @CellUnit(name = "Обработка возврата")
    private Double returnProcessing;
    @CellUnit(name = "Доставка возврата")
    private Double returnDelivery;
    //@CellUnit(name = "Услуга продвижения")
    //private Double promotion;
    @CellUnit(name = "Услуга продвижения (бонусы продавца)")
    private Double cashbackIndividualPoints;
    @CellUnit(name = "Компенсации (+)")
    private Double compensation;
    @CellUnit(name = "Продвижение в поиске")
    private Double searchPromotion;
    @CellUnit(name = "Трафареты")
    private Double stencilProduct;
    @CellUnit(name = "Ozon Премиум")
    private Double ozonPremium;
    @CellUnit(name = "Кросс-докинг")
    private Double crossDockingDelivery;
    //@CellUnit(name = "Начисления по претензиям")
    //private Double claimsAccruals;
    @CellUnit(name = "Прочие начисления")
    private Double other;
    @CellUnit(name = "Покупка отзывов")
    private Double buyReview;
    @CellUnit(name = "Утилизация")
    private Double disposal;
    @CellUnit(name="Итого", type = ExcelCellType.TOTAL, total = true)
    private final Double total = 0.0;

    @Builder
    public OZON_TableRow(String article,
                         Integer delivered,
                         Integer returned,
                         Double saleForDelivered,
                         Double sumReturn,
                         Double salesCommission,
                         Double shipmentProcessing,
                         Double logistic,
                         Double lastMile,
                         Double acquiring,
                         Double installment,
                         Double returnProcessing,
                         Double returnDelivery,
                         //Double promotion,
                         Double cashbackIndividualPoints,
                         Double compensation,
                         Double searchPromotion,
                         Double stencilProduct,
                         Double ozonPremium,
                         Double crossDockingDelivery,
                         //Double claimsAccruals,
                         Double other,
                         Double buyReview,
                         Double disposal) {
        super(article);
        this.delivered = delivered;
        this.returned = returned;
        this.saleForDelivered = saleForDelivered;
        this.sumReturn = sumReturn;
        this.salesCommission = salesCommission;
        this.shipmentProcessing = shipmentProcessing;
        this.logistic = logistic;
        this.lastMile = lastMile;
        this.acquiring = acquiring;
        this.installment = installment;
        this.returnProcessing = returnProcessing;
        this.returnDelivery = returnDelivery;
        //this.promotion = promotion;
        this.cashbackIndividualPoints = cashbackIndividualPoints;
        this.compensation = compensation;
        this.searchPromotion = searchPromotion;
        this.stencilProduct = stencilProduct;
        this.ozonPremium = ozonPremium;
        this.crossDockingDelivery = crossDockingDelivery;
        //this.claimsAccruals = claimsAccruals;
        this.other = other;
        this.buyReview = buyReview;
        this.disposal = disposal;
    }
}
