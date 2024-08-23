package com.example.consul;

import com.example.consul.dataframe.Dataframe;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DataFrameTest {
    @Test
    void toStringTest() {
        Dataframe df = Dataframe.createFromList(
                List.of("First Column", "Second Column", "Third Column"),
                List.of(
                        List.of("111111111111111", 2, 3),
                        List.of(1, 2, 3),
                        List.of(1, 2, 3)
                )
        );
        System.out.println(df);
    }
}
