package com.example.money.controller;

import com.example.money.validation.UserNameUnique;
import jakarta.validation.constraints.NotBlank;

public record UserForm(
        @NotBlank(message = "ユーザー名は必須です")
        @UserNameUnique
        String user_name,
        @NotBlank(message = "メールアドレスは必須です")
        String user_email,
        @NotBlank(message = "パスワードは必須です")
        String user_password
) {
}
