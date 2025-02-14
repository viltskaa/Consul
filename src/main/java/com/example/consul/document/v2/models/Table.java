package com.example.consul.document.v2.models;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.v2.utils.ObjectDeepReflection;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Table<T> {
    private String name;
    private List<T> data;
    private String startCell;

    public int getHeight() {
        return data.size() + (name != null ? 1 : 0) + 2;
    }

    public Class<?> getDataClass() {
        if (data == null || data.isEmpty()) {
            return null;
        }
        return data.get(0).getClass();
    }

    public int getDataSize() {
        return data.size();
    }

    public List<String> getHeader() {
        if (data == null || data.isEmpty()) {
            return null;
        }

        return ObjectDeepReflection.getFieldsWithAnnotation(
                data.get(0),
                CellUnit.class
        ).stream().map(field -> {
            CellUnit cellUnit = field.getAnnotation(CellUnit.class);
            if (cellUnit != null) {
                return cellUnit.name();
            } else {
                return null;
            }
        }).toList();
    }

    public List<String> getMainHeader() {
        if (data == null || data.isEmpty()) {
            return null;
        }

        return ObjectDeepReflection.getMainFieldsWithCellUnit(
                data.get(0)
        ).stream().map(field -> {
            CellUnit cellUnit = field.getAnnotation(CellUnit.class);
            if (cellUnit != null && cellUnit.detailed() && !cellUnit.total()) {
                return cellUnit.name();
            } else {
                return null;
            }
        }).toList();
    }

    public List<String> getSubHeader() {
        if (data == null || data.isEmpty()) {
            return null;
        }

        return ObjectDeepReflection.getSubFieldsWithCellUnit(
                data.get(0)
        ).stream().map(field -> {
            CellUnit cellUnit = field.getAnnotation(CellUnit.class);
            if (cellUnit != null && !cellUnit.detailed() && !cellUnit.total()) {
                return cellUnit.name();
            } else {
                return null;
            }
        }).toList();
    }
}
