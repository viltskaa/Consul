package com.example.consul.conditions;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Supplier;


@Component
public class ReportChecker {
    private Supplier<Boolean> function;

    public void init(Supplier<Boolean> function) {
        this.function = function;
    }

    public boolean start(Long delay) {
        if (function == null) {
            return false;
        }

        Executor delayed = CompletableFuture.delayedExecutor(delay, TimeUnit.SECONDS);
        try {
            CompletableFuture.supplyAsync(() -> function.get(), delayed).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
