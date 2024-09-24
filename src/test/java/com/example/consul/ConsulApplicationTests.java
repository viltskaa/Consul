package com.example.consul;

import com.example.consul.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConsulApplicationTests {
    @Test
    public void reportChecker() {
        DateUtils.WeekPeriod week = DateUtils.getNearMonday(2024, 3);
        System.out.println(week);

        System.out.println(week.getWeeks());
    }
}
