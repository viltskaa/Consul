package com.example.consul.api.utils;

import org.jetbrains.annotations.NotNull;

public class link {
    private String url = null;

    private link(@NotNull String url) {
        this.url = url;
    }

    @NotNull
    public static link create(@NotNull String url) {
        return new link(url);
    }

    @NotNull
    public link setArgs(@NotNull String ... args) {
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
