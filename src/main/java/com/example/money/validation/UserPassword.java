package com.example.money.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UserPasswordValidation.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserPassword {
    String message() default "ユーザー名もしくはパスワードが正しくありません。";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
}
