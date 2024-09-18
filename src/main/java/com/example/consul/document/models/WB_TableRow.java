package com.example.consul.document.models;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.annotations.TotalCell;
import com.example.consul.document.configurations.ExcelCellType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@TotalCell(
        formula = "(retailSum - sumReturn + sumCompensationForLost + sumCompensationForReplace + sumCompensationForDefected" +
                "- acquiringSale + acquiringReturn + additional - penalty " +
                "- logistic - deduction) / IF(retailAmount - returnAmount=0,1,retailAmount - returnAmount)"
)
public class WB_TableRow extends TableRow {
    @CellUnit(name = "Кол-во")
    private Integer retailAmount;
    @CellUnit(name = "Начислено")
    private Double retailSum;
    @CellUnit(name = "Возврат (Кол-во)", type = ExcelCellType.EXPENSIVE)
    private Integer returnAmount;
    @CellUnit(name = "Возврат (Сумма)")
    private Double sumReturn;
    @CellUnit(name = "Компенсация потерянного товара")
    private Double sumCompensationForLost;
    @CellUnit(name = "Компенсация замененного товара")
    private Double sumCompensationForReplace;
    @CellUnit(name = "Компенсация испорченного товара")
    private Double sumCompensationForDefected;
    @CellUnit(name = "поверенный (ПВЗ+эквайринг)")
    private Double acquiringSale;
    @CellUnit(name = "Возврат комиссии, поверенный", type = ExcelCellType.EXPENSIVE)
    private Double acquiringReturn;
    @CellUnit(name = "Доплаты", type = ExcelCellType.EXPENSIVE)
    private Double additional;
    @CellUnit(name = "штраф")
    private Double penalty;
    @CellUnit(name = "Прочие удержания")
    private Double deduction;
    @CellUnit(name = "Хранение(дашб)")
    private Double storage;
    @CellUnit(name = "Логистика")
    private Double logistic;
    @CellUnit(name = "Итого", type = ExcelCellType.TOTAL)
    private final Double total = 0.0;

    @Builder
    public WB_TableRow(String article,
                       Integer retailAmount,
                       Double retailSum,
                       Integer returnAmount,
                       Double sumReturn,
                       Double sumCompensationForLost,
                       Double sumCompensationForReplace,
                       Double sumCompensationForDefected,
                       Double acquiringSale,
                       Double acquiringReturn,
                       Double additional,
                       Double penalty,
                       Double deduction,
                       Double storage,
                       Double logistic) {
        super(article);
        this.retailAmount = retailAmount;
        this.retailSum = retailSum;
        this.returnAmount = returnAmount;
        this.sumReturn = sumReturn;
        this.sumCompensationForLost = sumCompensationForLost;
        this.sumCompensationForReplace = sumCompensationForReplace;
        this.sumCompensationForDefected = sumCompensationForDefected;
        this.acquiringSale = acquiringSale;
        this.acquiringReturn = acquiringReturn;
        this.additional = additional;
        this.penalty = penalty;
        this.deduction = deduction;
        this.storage = storage;
        this.logistic = logistic;
    }
}
