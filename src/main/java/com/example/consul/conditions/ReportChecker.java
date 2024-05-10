package com.example.consul.conditions;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Supplier;


@Component
public class ReportChecker {
    public Boolean start(Supplier<Boolean> function, Long delay) {
        if (function == null) {
            return false;
        }

        Executor delayed = CompletableFuture
                .delayedExecutor(delay, TimeUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            Boolean value = CompletableFuture
                    .supplyAsync(function, delayed)
                    .join();

            if (value) {
                return true;
            }
        }
        return false;
    }
}
