package com.example.consul.utils;

import com.example.consul.document.models.TableRow;
import com.google.gson.Gson;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Clustering {
    @Value("classpath:sorting.json")
    Resource sortingJson;

    public <T extends TableRow> Map<String, List<T>> of(@NotNull List<T> data) {
        try {
            String json = new String(Files.readAllBytes(sortingJson.getFile().toPath()));

            List<SortRules> sortingRules = List.of(new Gson()
                    .fromJson(
                            json.trim(),
                            SortRules[].class
                    ));

            Map<String, List<T>> output = new HashMap<>();
            for (SortRules rule: sortingRules) {
                List<T> selected = data.stream()
                        .filter(x -> {
                            String value = x.getArticle();
                            return rule.value.stream().anyMatch(
                                    art -> art.equalsIgnoreCase(value)
                            );
                        })
                        .toList();

                data.removeAll(selected);
                output.put(rule.key, selected);
            }

            return output;
        } catch (IOException | UnsupportedOperationException exception) {
            Map<String, List<T>> defaulted = new HashMap<>();
            defaulted.put("base", data);
            return defaulted;
        }
    }

    @Data
    private static class SortRules {
        private String key;
        private List<String> value;
    }
}
