package com.example.money.controller;

import com.example.money.validation.LabelNameUnique;
import jakarta.validation.constraints.NotBlank;

@LabelNameUnique
public record LabelForm(
        @NotBlank(message="支出項目を入力してから登録してください")
        String label_name,
        Integer user_id
) {
}
