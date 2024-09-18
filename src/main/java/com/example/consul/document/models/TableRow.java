package com.example.consul.document.models;

import com.example.consul.document.annotations.CellUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class TableRow {
    @CellUnit(name = "Артикул")
    protected String article;
}
