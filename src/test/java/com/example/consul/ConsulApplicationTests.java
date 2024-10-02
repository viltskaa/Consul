package com.example.consul;

import com.example.consul.utils.DateUtils;
import com.example.consul.utils.enumerate.Enumerate;
import com.example.consul.utils.enumerate.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ConsulApplicationTests {
    @Test
    public void reportChecker() {
        List<String> test = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9");
        for (Pair<String> s : Enumerate.of(test)) {
            System.out.println(s.getValue());
            System.out.println(s.getIndex());
        }
    }
}
