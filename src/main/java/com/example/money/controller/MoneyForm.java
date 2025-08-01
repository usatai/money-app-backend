package com.example.money.controller;

import com.example.money.enums.IncomeExpenditureType;

import jakarta.validation.constraints.NotNull;

public record MoneyForm(
        String date,
        String label_name,
        IncomeExpenditureType incomeExpenditureType,
        @NotNull(message = "金額を入力してください")
        Integer money_price,
        Integer user_id,
        String now,
        String yearDate
        
) {
}
