package com.example.money.controller;

<<<<<<< HEAD
import java.time.YearMonth;

=======
>>>>>>> 25ae53a99d216ac8b8d069c2a1f0994cb34402d4
import jakarta.validation.constraints.NotBlank;

public record DeleteForm(
        @NotBlank(message = "削除する項目を選択してください")
        String label_name,
<<<<<<< HEAD
        Integer user_id,
        YearMonth currentDate
=======
        Integer user_id
>>>>>>> 25ae53a99d216ac8b8d069c2a1f0994cb34402d4
) {
}
