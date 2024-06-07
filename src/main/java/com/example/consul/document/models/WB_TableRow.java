package com.example.consul.document.models;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.annotations.TotalCell;
import com.example.consul.document.configurations.ExcelCellType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@TotalCell(
        formula = "(retailSum - sumReturn + partSumCompensationForLost - compensation " +
                "- acquiringSale + acquiringReturn + additional - penalty " +
                "- logistic - deduction) / (retailAmount - returnAmount - amountCompensationForLost)"
)
public class WB_TableRow extends TableRow {
    @CellUnit(name="Кол-во")
    private Integer retailAmount;
    @CellUnit(name="Начислено")
    private Double retailSum;
    @CellUnit(name="Возврат (Кол-во)", type = ExcelCellType.EXPENSIVE)
    private Integer returnAmount;
    @CellUnit(name="Возврат (Сумма)")
    private Double sumReturn;
    @CellUnit(name="Сторно возвратов (кол-во)")
    private Double stornoReturn;
    @CellUnit(name="Сторно возвратов (сумма)")
    private Double sumStornoReturn;
    @CellUnit(name="Сторно продаж (кол-во)")
    private Double stornoSale;
    @CellUnit(name="Сторно продаж (сумма)")
    private Double stornoSumSale;
    @CellUnit(name="Полная компенсация потерянного товара (кол-во)")
    private Double amountCompensationForLost;
    @CellUnit(name="Полная компенсация потерянного товара (СУММА)")
    private Double allSumCompensationForLost;
    @CellUnit(name="Частичная компенсация за испорченный товар")
    private Double partSumCompensationForLost;
    @CellUnit(name="Комиссия")
    private Double compensation;
    @CellUnit(name="поверенный (ПВЗ+эквайринг)")
    private Double acquiringSale;
    @CellUnit(name="Возврат комиссии, поверенный", type = ExcelCellType.EXPENSIVE)
    private Double acquiringReturn;
    @CellUnit(name="Доплаты", type = ExcelCellType.EXPENSIVE)
    private Double additional;
    @CellUnit(name="штраф")
    private Double penalty;
    @CellUnit(name="Прочие удержания")
    private Double deduction;
    @CellUnit(name="Хранение(дашб)")
    private Double storage;
    @CellUnit(name="Логистика")
    private Double logistic;
    @CellUnit(name="Логистика сторно (+ нам)")
    private Double stornoLogistic;
    @CellUnit(name="Итого", type = ExcelCellType.TOTAL)
    private final Double total = 0.0;

    @Builder
    public WB_TableRow(String article,
                       Integer retailAmount,
                       Double retailSum,
                       Integer returnAmount,
                       Double sumReturn,
                       Double stornoReturn,
                       Double sumStornoReturn,
                       Double stornoSale,
                       Double stornoSumSale,
                       Double amountCompensationForLost,
                       Double allSumCompensationForLost,
                       Double partSumCompensationForLost,
                       Double compensation,
                       Double acquiringSale,
                       Double acquiringReturn,
                       Double additional,
                       Double penalty,
                       Double deduction,
                       Double storage,
                       Double logistic,
                       Double stornoLogistic) {
        super(article);
        this.retailAmount = retailAmount;
        this.retailSum = retailSum;
        this.returnAmount = returnAmount;
        this.sumReturn = sumReturn;
        this.stornoReturn = stornoReturn;
        this.sumStornoReturn = sumStornoReturn;
        this.stornoSale = stornoSale;
        this.stornoSumSale = stornoSumSale;
        this.amountCompensationForLost = amountCompensationForLost;
        this.allSumCompensationForLost = allSumCompensationForLost;
        this.partSumCompensationForLost = partSumCompensationForLost;
        this.compensation = compensation;
        this.acquiringSale = acquiringSale;
        this.acquiringReturn = acquiringReturn;
        this.additional = additional;
        this.penalty = penalty;
        this.deduction = deduction;
        this.storage = storage;
        this.logistic = logistic;
        this.stornoLogistic = stornoLogistic;
    }
}
