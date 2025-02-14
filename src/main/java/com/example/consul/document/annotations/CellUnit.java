package com.example.consul.document.annotations;

import com.example.consul.document.v1.configurations.ExcelCellType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value= ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface CellUnit {
    String name() default "";
    int width() default 16;
    boolean total() default false;
    ExcelCellType type() default ExcelCellType.BASE;
    String defaultValue() default "";
    boolean detailed() default true;
}
