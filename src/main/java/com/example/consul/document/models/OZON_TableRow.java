package com.example.consul.document.models;

import com.example.consul.document.Annotations.CellUnit;
import com.example.consul.document.configurations.ExcelCellType;
import lombok.*;

@Setter
@Getter
@Data
public class OZON_TableRow {
    @CellUnit(name = "Артикуль")
    private String offerId;
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
    @CellUnit(name = "Обработка отправлений", inverse = true)
    private Double shipmentProcessing;
    @CellUnit(name = "Логистика до покупателя", inverse = true)
    private Double logistic;
    @CellUnit(name = "Последняя миля", inverse = true)
    private Double lastMile;
    @CellUnit(name = "Эквайринг", inverse = true)
    private Double acquiring;
    @CellUnit(name = "Ozon рассрочка")
    private Double installment;
    @CellUnit(name = "Обработка возврата", inverse = true)
    private Double returnProcessing;
    @CellUnit(name = "Доставка возврата", inverse = true)
    private Double returnDelivery;
    @CellUnit(name = "Услуга продвижения")
    private Double promotion;
    @CellUnit(name = "Компенсация за испорченный товар")
    private Double compensation;
    @CellUnit(name = "Продвижение в поиске")
    private Double searchPromotion;
    @CellUnit(name = "Трафареты")
    private Double stencilProduct;
    @CellUnit(name = "Ozon Премиум")
    private Double ozonPremium;
    @CellUnit(name = "Кросс-докинг")
    private Double crossDockingDelivery;
    @CellUnit(name = "Начисления по претензиям")
    private Double claimsAccruals;
    @CellUnit(name = "Прочие начисления")
    private Double other;
    @CellUnit(name = "Итого", type = ExcelCellType.TOTAL)
    private Double total;

    public OZON_TableRow(String offerId,
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
                         Double promotion,
                         Double compensation,
                         Double searchPromotion,
                         Double stencilProduct,
                         Double ozonPremium,
                         Double crossDockingDelivery,
                         Double claimsAccruals,
                         Double other) {
        this.offerId = offerId;
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
        this.promotion = promotion;
        this.compensation = compensation;
        this.searchPromotion = searchPromotion;
        this.stencilProduct = stencilProduct;
        this.ozonPremium = ozonPremium;
        this.crossDockingDelivery = crossDockingDelivery;
        this.claimsAccruals = claimsAccruals;
        this.other = other;

        this.total = (saleForDelivered - sumReturn - salesCommission - (shipmentProcessing * -1) - (logistic * -1) - (lastMile * -1) - (acquiring * -1) - installment - (returnDelivery * -1) - (returnProcessing * -1) - promotion - stencilProduct - other) / (delivered - returned - returnProcessing - returnDelivery - promotion - stencilProduct) - 2.47;
    }
}
