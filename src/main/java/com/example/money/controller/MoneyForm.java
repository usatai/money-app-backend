package com.example.money.controller;

import jakarta.validation.constraints.NotNull;

public record MoneyForm(
        String label_name,
        @NotNull(message = "金額を入力してください")
        Integer money_price,
        Integer user_id,
        String date
) {
}
