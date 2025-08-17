package com.example.money.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class GuestIdSequenceDao {
    private final JdbcTemplate jdbc;

    public GuestIdSequenceDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public int nextPositive() {
        jdbc.update("INSERT INTO guest_id_seq VALUES (NULL)");
        Integer v = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        if (v == null) throw new IllegalStateException("LAST_INSERT_ID() failed");
        return v;
    }
    
}
