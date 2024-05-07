package com.example.consul.conditions;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
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
        CompletableFuture.supplyAsync(() -> {
                    if (function.get()) {
                        return true;
                    }
                    return null;
                }, delayed)
                .join();

        return true;
    }
}
