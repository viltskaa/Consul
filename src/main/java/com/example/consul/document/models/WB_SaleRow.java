package com.example.consul.document.models;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.annotations.TotalCell;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TotalCell(formula = "count")
public class WB_SaleRow extends TableRow {
    @CellUnit(name="Кол-во")
    private Long count;

    @Builder
    public WB_SaleRow(String article,
                      Long count) {
        super(article);
        this.count = count;
    }
}
