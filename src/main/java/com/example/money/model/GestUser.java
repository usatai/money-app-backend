package com.example.money.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "gest_users")
public class GestUser {

    @Id
    @Column
    private int user_id;

    @Column
    private String user_name;

    @Column(name="create_date")
    private Date create_date;

    @Column(name = "expires_at")
    private Date expires_at;
    
}
