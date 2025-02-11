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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ClassificationByArticle {
    @Value("classpath:bases.json")
    private Resource sortingJson;

    public <T extends TableRow> Map<String, List<T>> of(@NotNull List<T> data) {
        return of(data, "null");
    }

    public <T extends TableRow> Map<String, List<T>> of(@NotNull List<T> data, @NotNull String defaultKey) {
        try {
            ArrayList<T> dataCopy = new ArrayList<>(data);

            String json = new String(Files.readAllBytes(sortingJson.getFile().toPath()));

            List<SortRules> sortingRules = List.of(new Gson().fromJson(json.trim(), SortRules[].class));

            Map<String, List<T>> output = new HashMap<>();
            for (SortRules rule : sortingRules) {
                List<T> selected = dataCopy.stream()
                        .filter(x -> {
                            String value = x.getArticle();
                            String country = x.getCountry();

                            if (country!=null) {
                                boolean matchesArticle = rule.value.stream().anyMatch(
                                        art -> art.equalsIgnoreCase(value)
                                );
                                boolean matchesCountry = rule.country.equalsIgnoreCase(country);
                                return matchesArticle && matchesCountry;
                            } else {
                                return rule.value.stream().anyMatch(
                                        art -> art.equalsIgnoreCase(value)
                                );
                            }
                        })
                        .toList();

                dataCopy.removeAll(selected);
                output.put(rule.key, selected);
            }

            output.put(defaultKey, dataCopy);

            return output;
        } catch (IOException | UnsupportedOperationException exception) {
            Map<String, List<T>> defaulted = new HashMap<>();
            defaulted.put(defaultKey, data);
            return defaulted;
        }
    }

    @Data
    private static class SortRules {
        private String key;
        private String country;
        private List<String> value;
    }
}
