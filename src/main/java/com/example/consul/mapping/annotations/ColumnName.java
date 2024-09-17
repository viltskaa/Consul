package com.example.consul.mapping.annotations;

import com.example.consul.mapping.enums.ColumnNameEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value= ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface ColumnName {
    ColumnNameEnum name() default ColumnNameEnum.DEFAULT;
}
