package com.example.consul.document.v2.utils;

import com.example.consul.document.annotations.CellUnit;
import com.example.consul.document.v2.models.CellWithParams;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectDeepReflection {
    private ObjectDeepReflection() {
    }

    public static @NotNull List<Field> getFields(@NotNull Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            fields.addAll(0, Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    public static @NotNull List<Method> getMethods(@NotNull Class<?> cls) {
        List<Method> methods = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            methods.addAll(0, Arrays.asList(c.getDeclaredMethods()));
        }
        return methods;
    }

    public static List<Object> getValues(@NotNull Object obj, List<Field> fields) {
        List<Object> values = new ArrayList<>();
        List<Method> methods = getMethods(obj.getClass());

        for (Field field : fields) {
            Optional<Method> method = methods.stream()
                    .filter(
                            mth ->
                                    !mth.getAnnotatedReturnType().getType().equals(void.class)
                                            && mth.getName().equalsIgnoreCase("get" + field.getName())
                    ).findFirst();

            if (method.isEmpty()) {
                continue;
            }
            try {
                methods.remove(method.get());
                Object value = method.get().invoke(obj);
                values.add(value);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return values;
    }

    public static List<Field> getFieldsWithAnnotation(
            @NotNull Object obj,
            @NotNull Class<? extends Annotation> annotation
    ) {
        Class<?> cls = obj.getClass();
        return getFieldsWithAnnotation(cls, annotation);
    }

    public static List<Field> getFieldsWithAnnotation(
            @NotNull Class<?> cls,
            @NotNull Class<? extends Annotation> annotation
    ) {
        List<Field> fields = getFields(cls);

        return fields.stream()
                .filter(field -> field.isAnnotationPresent(annotation))
                .toList();
    }

    public static Map<Field, Object> get(@NotNull Object obj) {
        Class<?> cls = obj.getClass();
        final Map<Field, Object> map = new HashMap<>();

        List<Field> fields = getFields(cls);
        List<Method> methods = getMethods(cls);

        for (Field field : fields) {
            Optional<Method> method = methods.stream()
                    .filter(
                            mth ->
                                    !mth.getAnnotatedReturnType().getType().equals(void.class)
                                            && mth.getName().equalsIgnoreCase("get" + field.getName())
                    ).findFirst();

            if (method.isEmpty()) {
                continue;
            }
            try {
                methods.remove(method.get());
                Object value = method.get().invoke(obj);
                map.put(field, value);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    public static List<CellWithParams> getCells(@NotNull Object obj) {
        final Map<Field, Object> map = get(obj);
        return new ArrayList<>(map.entrySet().stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(CellUnit.class))
                .map(
                        entry -> {
                            CellUnit cellUnit = entry.getKey().getAnnotation(CellUnit.class);

                            return CellWithParams.builder()
                                    .name(cellUnit.name())
                                    .fieldName(entry.getKey().getName())
                                    .value(entry.getValue())
                                    .width(cellUnit.width())
                                    .type(cellUnit.type())
                                    .total(cellUnit.total())
                                    .defaultValue(cellUnit.defaultValue())
                                    .build();
                        }
                ).toList());
    }
}
