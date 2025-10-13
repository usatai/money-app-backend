package com.example.money.controller;

import com.example.money.validation.UserNameUnique;

import jakarta.validation.constraints.NotBlank;

public record GestLoginUserForm (
    @NotBlank(message = "ユーザー名は必須です")
    @UserNameUnique
    String gestLoginUserName
){
}
