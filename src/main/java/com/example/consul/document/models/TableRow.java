package com.example.consul.document.models;

import com.example.consul.document.annotations.CellUnit;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class TableRow {
    @CellUnit(name = "Артикул", total = false)
    protected String article;
}
