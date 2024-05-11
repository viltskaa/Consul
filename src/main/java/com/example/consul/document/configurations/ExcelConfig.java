package com.example.consul.document.configurations;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
public class ExcelConfig<T> {
    private String fileName;
    private List<String> sheetsName;
    private List<List<T>> data;
    private HeaderConfig header;
    private Integer pageNumber = 1;

    public static <T> ExcelConfig<T>.Builder builder() {
        return new ExcelConfig<T>().new Builder();
    }

    public class Builder {
        private Builder() {}

        public Builder fileName(@NotNull String fileName) {
            ExcelConfig.this.fileName = fileName;
            return this;
        }

        public Builder sheetsName(@NotNull List<String> sheetsName) {
            ExcelConfig.this.sheetsName = sheetsName;
            return this;
        }

        public Builder data(@NotNull List<List<T>> data) {
            ExcelConfig.this.data = data;
            ExcelConfig.this.pageNumber = data.size();
            return this;
        }

        public Builder header(@NotNull HeaderConfig header) {
            ExcelConfig.this.header = header;
            return this;
        }

        public ExcelConfig<T> build() {
            return ExcelConfig.this;
        }
    }
}
