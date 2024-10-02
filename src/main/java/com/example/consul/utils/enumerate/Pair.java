package com.example.consul.utils.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<T> {
    private T value;
    private Integer index;
}