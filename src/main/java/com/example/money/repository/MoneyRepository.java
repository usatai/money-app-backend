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

    @Query(value = "select label_id from label where user_id = :userIdInt and label_name = :label_name and TO_CHAR(create_date,'YYYY-MM') = TO_CHAR(CAST(:create_date AS DATE), 'YYYY-MM')", nativeQuery = true)
    Integer userGetLabelId(@Param("userIdInt")Integer userIdInt,@Param("label_name")String user_label,@Param("create_date") Date sqlDate);

    @Query(value = "SELECT m.user_id,SUM(m.money_price)" + 
           "FROM money m " + "WHERE m.income_expenditure_type = 'EXPENDITURE' " +
           "AND m.user_id IN (SELECT g.user_id FROM goal_expenditure g) " +
           "AND EXTRACT(YEAR FROM m.created_date) = EXTRACT(YEAR FROM CURRENT_DATE) " + 
           "AND EXTRACT(MONTH FROM m.created_date) = EXTRACT(MONTH FROM CURRENT_DATE) " +  
           "GROUP BY m.user_id",nativeQuery = true)
    List<Object[]> sumExpenditureByUserWithGoal();
}
