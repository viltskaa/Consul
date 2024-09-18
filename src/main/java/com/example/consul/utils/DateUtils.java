package com.example.consul.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {
    public static Month getMonth(@NotNull Integer year, @NotNull Integer month) {
        LocalDate date = LocalDate.of(year, month, 1);

        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        String startOfMonthString = startOfMonth.atStartOfDay(ZoneOffset.UTC)
                .toString().replace("T00:00", "T00:00:00.000");
        String endOfMonthString = endOfMonth.atStartOfDay(ZoneOffset.UTC)
                .plusDays(1).minusNanos(1000000).toString();

        return Month.builder()
                .month(month)
                .year(year)
                .firstDay(startOfMonthString)
                .lastDay(endOfMonthString)
                .build();
    }

    public static Week getWeek(@NotNull Integer year, @NotNull Integer month, @NotNull Integer weekNumber) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstMonday = yearMonth.atDay(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

        LocalDate startOfWeek = firstMonday.plusWeeks(weekNumber - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        return Week.builder()
                .week(weekNumber)
                .firstDay(startOfWeek.toString())
                .lastDay(endOfWeek.toString())
                .build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Week {
        private int week;
        private String firstDay;
        private String lastDay;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Month {
        private int month;
        private int year;
        private String firstDay;
        private String lastDay;
    }
}
