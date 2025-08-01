package com.example.money.controller;

import java.time.YearMonth;

import com.example.money.enums.IncomeExpenditureType;
import com.example.money.validation.LabelNameUnique;
import jakarta.validation.constraints.NotBlank;

@LabelNameUnique
public record LabelForm(
        @NotBlank(message="支出項目を入力してから登録してください")
        String label_name,
        Integer user_id,
        IncomeExpenditureType type,
        YearMonth currentDate
) {
}
