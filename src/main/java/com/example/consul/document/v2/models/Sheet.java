package com.example.consul.document.v2.models;

import lombok.*;

import java.util.Arrays;
import java.util.List;

@Setter
@Getter
public class Sheet<T> {
    private String name;
    private List<Table<T>> tables;

    public static <T> Sheet<T>.Builder builder() {
        return new Sheet<T>().new Builder();
    }

    public class Builder {
        public Builder name(String name) {
            Sheet.this.name = name;
            return this;
        }

        @SafeVarargs
        public final Builder tables(Table<T>... tables) {
            Sheet.this.tables = Arrays.stream(tables).toList();
            return this;
        }

        public final Builder tables(List<Table<T>> tables) {
            Sheet.this.tables = tables;
            return this;
        }

        public Sheet<T> build() {
            return Sheet.this;
        }
    }
}
