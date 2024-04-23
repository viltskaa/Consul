package com.example.consul.mapping;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ListToHtml {
    private ListToHtml() {

    }

    public static <T> String build(@NotNull List<T> objects) {
        StringBuilder output = new StringBuilder(String.format("<table class='%s'>", "table"));
        List<Field> fields = Arrays.stream(objects.get(0).getClass().getDeclaredFields()).toList();
        fields.forEach(x -> x.setAccessible(true));

        output.append("<thead><tr>");
        for (String header: fields.stream().map(Field::getName).toList()) {
            output.append("<td scope='col'>").append(header).append("</td>");
        }
        output.append("</tr></thead>").append("<tbody>");
        for (Object object : objects) {
            output.append("<tr>");
            for (Field field : fields) {
                try {
                    output.append("<td>").append(
                            field.get(object).toString()
                    ).append("</td>");
                } catch (IllegalAccessException e) {
                    output.append("<td>").append(
                            "CGA"
                    ).append("</td>");
                }
            }
            output.append("</tr>");
        }
        output.append("</tbody>").append("</table>");

        return output.toString();
    }
}
