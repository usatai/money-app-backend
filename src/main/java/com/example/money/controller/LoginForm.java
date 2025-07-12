package com.example.money.controller;

import com.example.money.validation.UserPassword;
import jakarta.validation.constraints.NotBlank;

@UserPassword
public record LoginForm(
        @NotBlank(message="ユーザー名は必須です")
        String loginUser_name,

        @NotBlank(message="パスワードは必須です")
        String loginUser_password
) {
}
