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
import java.util.ArrayList;
import java.util.List;

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
                .firstDayOf(startOfWeek)
                .lastDayOf(endOfWeek)
                .build();
    }

    public static WeekPeriod getNearMonday(@NotNull Integer year, @NotNull Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate nextMonday = yearMonth.atDay(1)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        LocalDate prevMonday = yearMonth.atDay(1)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        if (prevMonday.isBefore(nextMonday)) {
            return WeekPeriod.builder()
                    .firstDay(prevMonday.toString())
                    .lastDay(prevMonday.plusDays(27).toString())
                    .firstDayOf(prevMonday)
                    .lastDayOf(prevMonday.plusDays(27))
                    .build();
        }
        return WeekPeriod.builder()
                .firstDay(nextMonday.toString())
                .lastDay(nextMonday.plusDays(27).toString())
                .firstDayOf(nextMonday)
                .lastDayOf(nextMonday.plusDays(27))
                .build();
    }

    public static Boolean isAfterDateStringCompare(@NotNull String first, @NotNull String second) {
        LocalDate firstDate = LocalDate.parse(first);
        LocalDate secondDate = LocalDate.parse(second);
        return firstDate.isAfter(secondDate) || firstDate.isEqual(secondDate);
    }

    public static Boolean isBeforeDateStringCompare(@NotNull String first, @NotNull String second) {
        LocalDate firstDate = LocalDate.parse(first);
        LocalDate secondDate = LocalDate.parse(second);
        return firstDate.isBefore(secondDate) || firstDate.isEqual(secondDate);
    }

    public static Boolean isInRangeStringCompare(
            @NotNull String first,
            @NotNull String startRange,
            @NotNull String endRange
    ) {
        LocalDate firstDate = LocalDate.parse(first);
        LocalDate startDate = LocalDate.parse(startRange);
        LocalDate endDate = LocalDate.parse(endRange);
        return (firstDate.isAfter(startDate) || firstDate.isEqual(startDate))
                && (firstDate.isBefore(endDate) || firstDate.isEqual(endDate));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class WeekPeriod {
        private String firstDay;
        private String lastDay;
        private LocalDate firstDayOf;
        private LocalDate lastDayOf;

        public List<Week> getWeeks() {
            List<Week> weeks = new ArrayList<>();
            if (firstDayOf == null) return weeks;

            LocalDate localFirstDayOf = LocalDate.of(
                    firstDayOf.getYear(),
                    firstDayOf.getMonth(),
                    firstDayOf.getDayOfMonth()
            );

            for (int i = 0; i < 4; i++) {
                Week weekLocal = Week.builder()
                        .week(i + 1)
                        .firstDay(localFirstDayOf.toString())
                        .lastDay(localFirstDayOf.plusDays(6).toString())
                        .firstDayOf(localFirstDayOf)
                        .lastDayOf(localFirstDayOf.plusDays(6))
                        .build();

                weeks.add(weekLocal);
                localFirstDayOf = localFirstDayOf.plusDays(7);
            }

            if (lastDayOf.plusDays(7).getMonthValue() == localFirstDayOf.getMonthValue()) {
                Week weekLocal = Week.builder()
                        .week(5)
                        .firstDay(localFirstDayOf.toString())
                        .lastDay(localFirstDayOf.plusDays(6).toString())
                        .firstDayOf(localFirstDayOf)
                        .lastDayOf(localFirstDayOf.plusDays(6))
                        .build();

                weeks.add(weekLocal);
            }

            return weeks;
        };
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Week {
        private int week;
        private String firstDay;
        private String lastDay;
        private LocalDate firstDayOf;
        private LocalDate lastDayOf;

        @Override
        public String toString() {
            return firstDay + " - " + lastDay;
        }

        public boolean isInRange(@NotNull String day) {
            LocalDate date = LocalDate.parse(day);
            return !(date.isBefore(firstDayOf) || date.isAfter(lastDayOf));
        }
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
