package com.example.consul.document.models;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.annotations.TotalCell;
import com.example.consul.document.v1.configurations.ExcelCellType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TotalCell(
        formula = "(saleSum - returnSum + compensationLost - commission + returnCommission" +
                "- penalty - deduction - storageFee - logistic " +
                "- storno) / IF(saleCount - returnCount=0,1,saleCount - returnCount)"
)
public class WB_TableRow extends TableRow {
    @CellUnit(name = "Кол-во")
    private Integer saleCount;
    @CellUnit(name = "Начислено")
    private Double saleSum;
    @CellUnit(name = "Возврат (Кол-во)", type = ExcelCellType.EXPENSIVE)
    private Integer returnCount;
    @CellUnit(name = "Возврат (Сумма)")
    private Double returnSum;
    @CellUnit(name = "Полная/частичная компенсация потерянного/замененного товара")
    private Double compensationLost;
    @CellUnit(name = "Количество потерянного/замененного товара")
    private Integer countLost;
    @CellUnit(name = "Комиссия маркетплейса (Вознаграждение ВБ/Возмещ.ПВЗ/эквайринг)")
    private Double commission;
    @CellUnit(name = "Возврат комиссии маркетплейса (вознаграждения ВБ/Возмещ.ПВЗ/эквайринг)")
    private Double returnCommission;
    @CellUnit(name = "Штрафы")
    private Double penalty;
    @CellUnit(name = "Прочие удержания")
    private Double deduction;
    @CellUnit(name = "Хранение(дашб)")
    private Double storageFee;
    @CellUnit(name = "Логистика")
    private Double logistic;
    @CellUnit(name = "Логистика сторно (+ нам)")
    private Double storno;
    @CellUnit(name = "Итого", type = ExcelCellType.TOTAL, total = true)
    private final Double total = 0.0;

    @Builder
    public WB_TableRow(
            String article,
            Integer saleCount,
            Double saleSum,
            Integer returnCount,
            Double returnSum,
            Double compensationLost,
            Integer countLost,
            Double commission,
            Double returnCommission,
            Double penalty,
            Double deduction,
            Double storageFee,
            Double logistic,
            Double storno
    ) {
        super(article);
        this.saleCount = saleCount;
        this.saleSum = saleSum;
        this.returnCount = returnCount;
        this.returnSum = returnSum;
        this.compensationLost = compensationLost;
        this.countLost = countLost;
        this.commission = commission;
        this.returnCommission = returnCommission;
        this.penalty = penalty;
        this.deduction = deduction;
        this.storageFee = storageFee;
        this.logistic = logistic;
        this.storno = storno;
    }
}
