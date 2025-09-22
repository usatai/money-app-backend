package com.example.money.repository;

import org.springframework.stereotype.Repository;

import com.example.money.model.GoalExpenditure;

import org.springframework.data.repository.query.Param;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface GoalExpenditureRepository extends JpaRepository<GoalExpenditure,Long>{
    
    @Query(value = "SELECT COUNT(*) FROM goal_expenditure WHERE user_id = :user_id AND DATE_FORMAT(updated_at, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')",nativeQuery = true)
    public Long findLoginCheck(@Param("user_id") Integer userIdInt);
    
    @Query(value = "SELECT * FROM goal_expenditure WHERE user_id = :user_id",nativeQuery = true)
    public GoalExpenditure findByUser(@Param("user_id") Integer userIdInt);
}
