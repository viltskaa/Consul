package com.example.consul.utils.enumerate;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Enumerate<T> implements Iterable<Pair<T>> {
    private final List<T> data;

    private Enumerate(List<T> data) {
        this.data = data;
    }

    /**
     * @param data generic list of user data
     * @return Object like Python Enumerate. Use with for-each
     * @param <T> generic type of list element
     */
    public static <T> Enumerate<T> of(List<T> data) {
        return new Enumerate<>(data);
    }

    /**
     * Returns an iterator over elements of type {@code Pair<T>}.
     * <p> {@code Pair.getValue()} - return {@code T} obj
     * <p> {@code Pair.getIndex()} - return {Integer} index in user list
     *
     * @return an {@code Iterator<Pair<T>>}.
     */
    @Override
    public @NotNull Iterator<Pair<T>> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<Pair<T>> {
        private int cursor;

        Itr() {}

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return cursor < Enumerate.this.data.size();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Pair<T> next() {
           int i = cursor;
           if (i >= Enumerate.this.data.size()) {
               throw new NoSuchElementException();
           }
           cursor = i + 1;
           return new Pair<>(Enumerate.this.data.get(i), i);
        }
    }
}
