package com.example.money.repository;

import com.example.money.model.GestUser;
import com.example.money.model.User;

import java.util.Date;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GestUserRepository extends JpaRepository<User, Integer> {

    @Query(value = "select user_id from user where user_name = :user_name",nativeQuery = true)
    Optional<Integer> gest_user_id(@Param("user_name") String user_name);

    @Modifying
    @Query(value = "delete from user where expires_at is not null and expires_at < :now",nativeQuery = true)
    int deleteExpired(@Param("now") Date now);
} 