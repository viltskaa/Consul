package com.example.consul.document.models;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.annotations.TotalCell;
import com.example.consul.document.configurations.ExcelCellType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TotalCell(
        formula = "(saleForDelivered-sumReturn-salesCommission-shipmentProcessing" +
                "-logistic-lastMile-acquiring-installment-returnProcessing-returnDelivery" +
                "-promotion-compensation-searchPromotion-stencilProduct-ozonPremium" +
                "-crossDockingDelivery-claimsAccruals-other)/(delivered-returned)-2.47"
)
public class OZON_TableRow {
    @CellUnit(name = "Артикул", total = false)
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
    @CellUnit(name="Итого", type = ExcelCellType.TOTAL)
    private final Double total = 0.0;
}
