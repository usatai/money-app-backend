package com.example.money.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="label")
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="label_id")
    private int label_id;

    @Column(name="label_name")
    private String label_name;

    @Column(name="user_id")
    private int user_id;

    @Column(name="create_date")
    private Date create_date;

}
