package com.example.money.controller;

import jakarta.validation.constraints.NotBlank;

public record DeleteForm(
        @NotBlank(message = "削除する項目を選択してください")
        String label_name,
        Integer user_id
) {
}
