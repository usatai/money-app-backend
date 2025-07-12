package com.example.money.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private int user_id;

    @Column(name="user_name")
    private String user_name;

    @Column(name="user_email")
    private String user_email;

    @Column(name="user_password")
    private String user_password;

    @Column(name="create_date")
    private Date create_date;

}
