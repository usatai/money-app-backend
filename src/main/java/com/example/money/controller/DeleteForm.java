package com.example.money.controller;

import java.time.YearMonth;

import jakarta.validation.constraints.NotBlank;

public record DeleteForm(
        @NotBlank(message = "削除する項目を選択してください")
        String label_name,
        Integer user_id,
        YearMonth currentDate
) {
}
