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
        formula = "(retailSum - sumReturn + partSumCompensationForLost - compensation " +
                "- acquiringSale + acquiringReturn + acquiringPvzReturn + additional - penalty " +
                "- logistic - deduction) / (retailAmount - returnAmount - amountCompensationForLost)"
)
public class WB_TableRow {
    @CellUnit(name="Артикул", total = false)
    private String saName;
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
    @CellUnit(name="Возврат комиссии (+нам)", type = ExcelCellType.EXPENSIVE)
    private Double acquiringReturn;
    @CellUnit(name="Возврат поверенный (+нам)", type = ExcelCellType.EXPENSIVE)
    private Double acquiringPvzReturn;
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
}
