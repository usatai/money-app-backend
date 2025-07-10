package com.example.money.repository;

import com.example.money.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    //ユーザーID抽出
    @Query(value="select user_id from users where user_name = :user_name",nativeQuery = true)
    Optional<Integer> user_id(@Param("user_name") String user_name);

    //同名のユーザーの場合1(TRUE)を返す
    @Query(value="SELECT EXISTS(SELECT 1 FROM users WHERE user_name = :user_name)", nativeQuery = true)
    Long existsByUser(@Param("user_name") String user_name);

    @Query(value="SELECT user_password FROM users WHERE user_name = :user_name",nativeQuery = true)
    String user_password(@Param("user_name") String user_name);
}
