package com.example.consul.dto.OZON;

import com.example.consul.document.Annotations.CellUnit;
import com.example.consul.document.configurations.ExcelCellType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class OZON_TableRow {
    @CellUnit(name="Артикуль")
    private String offerId;
    @CellUnit(name="Доставлено")
    private Integer delivered;
    @CellUnit(name="Возвращено")
    private Integer Returned;
    @CellUnit(name="Начислено за доставленный товар")
    private Double saleForDelivered;
    @CellUnit(name="Возврат", type = ExcelCellType.EXPENSIVE)
    private Double sumReturn;
    @CellUnit(name="Комиссия за продажу", type = ExcelCellType.EXPENSIVE)
    private Double salesCommission;
    @CellUnit(name="Обработка отправлений", inverse = true)
    private Double shipmentProcessing;
    @CellUnit(name="Логистика до покупателя", inverse = true)
    private Double logistic;
    @CellUnit(name="Последняя миля", inverse = true)
    private Double lastMile;
    @CellUnit(name="Эквайринг", inverse = true)
    private Double acquiring;
    @CellUnit(name="Ozon рассрочка")
    private Double installment;
    @CellUnit(name="Обработка возврата", inverse = true)
    private Double returnProcessing;
    @CellUnit(name="Доставка возврата", inverse = true)
    private Double returnDelivery;
    @CellUnit(name="Услуга продвижения")
    private Double promotion;
    @CellUnit(name="Компенсация за испорченный товар")
    private Double compensation;
    @CellUnit(name="Продвижение в поиске")
    private Double searchPromotion;
    @CellUnit(name="Трафареты")
    private Double stencilProduct;
    @CellUnit(name="Ozon Премиум")
    private Double ozonPremium;
    @CellUnit(name="Кросс-докинг")
    private Double crossDockingDelivery;
    @CellUnit(name="Начисления по претензиям")
    private Double claimsAccruals;
    @CellUnit(name="Прочие начисления")
    private Double other;
    @CellUnit(name="Итого")
    private Double total;
}
