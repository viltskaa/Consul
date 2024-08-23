package com.example.consul.dataframe;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

@Setter
@Getter
public class Row {
    private List<Object> elements;

    private Row() {}

    public Row(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        elements = List.of(new Object[fields.length]);

        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                elements.set(i, fields[i].get(obj));
            } catch (IllegalAccessException e) {
                elements.set(i, null);
            }
        }
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @NotNull
    public Iterator<Object> iterator() {
        return elements.iterator();
    }

    @NotNull
    public Object @NotNull [] toArray() {
        return elements.toArray();
    }

    @NotNull
    public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return elements.toArray(a);
    }

    @NotNull
    public Row remove(Object o) {
        elements.remove(o);
        return this;
    }

    @NotNull
    public Row pop(int index) {
        elements.remove(index);
        return this;
    }

    @NotNull
    public Row pop(int ... index) {
        for (int i = 0; i < index.length; i++) {
            pop(index[i] - i);
        }
        return this;
    }

    @NotNull
    public Row pop(List<Integer> index) {
        for (int i = 0; i < index.size(); i++) {
            pop(index.get(i) - i);
        }
        return this;
    }

    public boolean containsAll(@NotNull Collection<Object> c) {
        return c.containsAll(elements);
    }

    @NotNull
    public Row addAll(@NotNull Collection<?> c) {
        elements.addAll(c);
        return this;
    }

    @NotNull
    public Row addAll(int index, @NotNull Collection<?> c) {
        elements.addAll(index, c);
        return this;
    }

    @NotNull
    public Row removeAll(@NotNull Collection<?> c) {
        elements.removeAll(c);
        return this;
    }

    @NotNull
    public Row retainAll(@NotNull Collection<?> c) {
        elements.retainAll(c);
        return this;
    }

    public void clear() {
        elements.clear();
    }

    public Object get(int index) {
        if (index < elements.size() && index >= 0) {
            return elements.get(index);
        }
        if (index < 0 && elements.size() + index >= 0) {
            return elements.get(elements.size() + index);
        }
        return null;
    }

    public Object set(int index, Object element) {
        int size = elements.size();
        if ((index < size && index >= 0) || (index < 0 && size + index >= 0)) {
            int updatedIndex = (index < 0 && size + index >= 0) ? index + size : index;
            Object oldValue = elements.get(updatedIndex);
            elements.set(updatedIndex, element);
            return oldValue;
        }
        return null;
    }

    @NotNull
    public Row add(Object element) {
        elements.add(element);
        return this;
    }

    @NotNull
    public Row add(int index, Object element) {
        elements.add(index, element);
        return this;
    }

    @NotNull
    public Row remove(int index) {
        elements.remove(index);
        return this;
    }

    public int indexOf(Object o) {
        return elements.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return elements.lastIndexOf(o);
    }

    @NotNull
    public ListIterator<Object> listIterator() {
        return elements.listIterator();
    }

    @NotNull
    public ListIterator<Object> listIterator(int index) {
        return elements.listIterator(index);
    }

    @NotNull
    public static Row fromList(List<Object> data) {
        Row row = new Row();
        row.setElements(data);
        return row;
    }

    @NotNull
    public Row subList(int fromIndex, int toIndex) {
        return Row.fromList(elements.subList(fromIndex, toIndex));
    }

    @NotNull
    public Row slice(int fromIndex, int toIndex, int step) {
        Row row = new Row();
        for (int i = fromIndex; i < toIndex; i += step) {
            row.add(elements.get(i));
        }
        return row;
    }
}
