package com.example.consul.api.utils;

import org.jetbrains.annotations.NotNull;

public class Link {
    private String url;

    private Link(@NotNull String url) {
        this.url = url;
    }

    @NotNull
    public static Link create(@NotNull String url) {
        return new Link(url);
    }

    @NotNull
    public Link setArgs(@NotNull String ... args) {
        for (String arg: args) {
            this.url = this.url.replaceFirst("<arg>", arg);
        }

        return this;
    }

    @NotNull
    public String build() {
        return this.url;
    }
}
