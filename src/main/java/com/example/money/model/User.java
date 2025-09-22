package com.example.money.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer user_id;

    @Transient
    private boolean newEntity = false;

    @Column(name="user_name")
    private String user_name;

    @Column(name="user_email")
    private String user_email;

    @Column(name="user_password")
    private String user_password;

    @Column(name="created_date")
    private Date created_date;

    @Column(name = "expires_at")
    private Date expires_at;

}
