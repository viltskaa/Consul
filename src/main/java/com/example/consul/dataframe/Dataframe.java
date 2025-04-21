package com.example.consul.dataframe;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Getter
@Setter
public class Dataframe {
    private List<String> headers;
    private List<Row> data;

    private Dataframe(List<String> headers,
                      List<Row> data) {
        this.headers = headers;
        this.data = data;
    }

    public static Dataframe create(List<String> headers, List<Object> data) {
        List<Row> rows = data.stream().map(Row::new).toList();
        return new Dataframe(headers, rows);
    }

    public static Dataframe createFromList(List<String> headers, List<List<Object>> data) {
        List<Row> rows = data.stream().map(Row::fromList).toList();
        return new Dataframe(headers, rows);
    }

    public static Dataframe create(Map<String, Object> data) {
        return new Dataframe(
                data.keySet().stream().toList(),
                data.values().stream().map(Row::new).toList()
        );
    }

//    public Dataframe filterRows(String predicate) {
//        data = data.stream().filter(predicate).toList();
//        return this;
//    }

    public Dataframe filterColumns(String ... columns) {
        List<Integer> indexes = new ArrayList<>();
        List<String> newHeaders = new ArrayList<>();
        List<String> headersNeeds = new ArrayList<>(List.of(columns));

        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            if (!headersNeeds.contains(header)) {
                indexes.add(i);
            }
            else {
                newHeaders.add(header);
                headersNeeds.remove(header);
            }
        }

        data.forEach(row -> row.pop(indexes));
        return new Dataframe(newHeaders, data);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        List<Integer> maxLengthColumn = new ArrayList<>();

        for (String header : headers) {
            String formatedString = "%" + header.length() + "s ";
            builder.append(formatedString.formatted(header));
            maxLengthColumn.add(header.length());
        }
        builder.append('\n');
        for (Row datum : data) {
            for (int j = 0; j < headers.size(); j++) {
                Integer maxLength = maxLengthColumn.get(j);
                String value = datum.get(j).toString();
                if (value.length() > maxLength) {
                    value = value.substring(0, maxLength - 3) + "...";
                }
                builder.append(
                        ("%" + maxLength + "s ").formatted(value)
                );
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
