package com.example.money.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.money.enums.IncomeExpenditureType;


import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="money")
public class Money {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="money_id")
    private int money_id;

    @Column(name="money_price")
    private int money_price;

    @Enumerated(EnumType.STRING)
    @Column(name="income_expenditure_type")
    private IncomeExpenditureType incomeExpenditureType;

    @Column(name="user_id")
    private int user_id;

    @Column(name="label_id")
    private int label_id;

    @Column(name="create_date")
    private Date create_date;
}
