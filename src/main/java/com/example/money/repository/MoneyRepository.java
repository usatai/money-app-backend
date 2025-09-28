package com.example.money.repository;

import com.example.money.model.Money;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MoneyRepository extends JpaRepository<Money,Integer> {

    @Query(value = "select label_id from label where user_id = :userIdInt and label_name = :label_name and DATE_FORMAT(create_date,'%Y-%m') = DATE_FORMAT(:create_date,'%Y-%m')",nativeQuery = true)
    Integer userGetLabelId(@Param("userIdInt")Integer userIdInt,@Param("label_name")String user_label,@Param("create_date") Date sqlDate);

    @Query(value = "SELECT m.user_id,SUM(m.money_price)" + 
           "FROM money m " + "WHERE m.income_expenditure_type = 'EXPENDITURE' " +
           "AND m.user_id IN (SELECT g.user_id FROM goal_expenditure g) " +
           "AND YEAR(m.created_date) = YEAR(CURRENT_DATE()) " + 
           "AND MONTH(m.created_date) = MONTH(CURRENT_DATE()) " +  
           "GROUP BY m.user_id",nativeQuery = true)
    List<Object[]> sumExpenditureByUserWithGoal();
}
