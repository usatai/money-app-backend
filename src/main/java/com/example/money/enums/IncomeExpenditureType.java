package com.example.money.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IncomeExpenditureType {
    INCOME("収入"),
    EXPENDITURE("支出");

    private final String displayValue;

    IncomeExpenditureType(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonCreator
    public static IncomeExpenditureType fromDisplayValue(String text) {
        for (IncomeExpenditureType type : IncomeExpenditureType.values()) {
            if (type.displayValue.equals(text)) { 
                return type;
            }

            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown income/expenditure type: " + text);
    } 
}
