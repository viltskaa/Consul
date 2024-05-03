package com.example.consul.document.configurations;

import com.example.consul.document.models.HeaderConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
public class ExcelConfig<T> {
    private String fileName;
    private List<String> sheetName;
    private List<T> data;
    private HeaderConfig header;
    private Integer pageNumber = 1;

    public ExcelConfig(@NotNull String fileName,
                       @NotNull List<String> sheetName,
                       @NotNull HeaderConfig header,
                       @NotNull List<T> data) {
        this.fileName = fileName;
        this.sheetName = sheetName;
        this.header = header;
        this.data = data;

        if (this.confirm()) {
            throw new IllegalArgumentException(
                    "Invalid Excel configuration"
            );
        }
        if (data.get(0) instanceof List<?>) {
            pageNumber = data.size();
        }
    }

    public Boolean confirm() {
        if (fileName.isEmpty())
            return false;
        if (sheetName.isEmpty())
            return false;
        return data.isEmpty();
    }

    public Class<?> getDataClass() {
        if (data == null || data.isEmpty())
            return null;
        return data.get(0).getClass();
    }
}
