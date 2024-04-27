package com.example.consul.document.configurations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExcelConfig<T> {
    private String fileName;
    private String sheetName;
    private String headerName;
    private List<T> data;

    public Boolean confirm() {
        if (fileName == null || fileName.isEmpty())
            return false;
        if (headerName == null || headerName.isEmpty())
            return false;
        if (sheetName == null || sheetName.isEmpty())
            return false;
        return data != null && !data.isEmpty();
    }
    public Class<?> getDataClass() {
        if (data == null || data.isEmpty())
            return null;
        return data.get(0).getClass();
    }
}
