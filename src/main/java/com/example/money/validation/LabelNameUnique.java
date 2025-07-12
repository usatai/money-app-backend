package com.example.money.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LabelNameUniqueValidator.class)
public @interface LabelNameUnique {
    String message() default "入力した値はすでに項目として存在しております";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
