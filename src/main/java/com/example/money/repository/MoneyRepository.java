package com.example.money.repository;

import com.example.money.model.Money;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface MoneyRepository extends JpaRepository<Money,Integer> {

    @Query(value = "select label_id from label where user_id = :userIdInt and label_name = :label_name and DATE_FORMAT(create_date,'%Y-%m') = DATE_FORMAT(:create_date,'%Y-%m')",nativeQuery = true)
    Integer userGetLabelId(@Param("userIdInt")Integer userIdInt,@Param("label_name")String user_label,@Param("create_date") Date sqlDate);
}
