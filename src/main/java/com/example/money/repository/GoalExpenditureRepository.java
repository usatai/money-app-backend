package com.example.money.repository;

import org.springframework.stereotype.Repository;

import com.example.money.model.GoalExpenditure;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface GoalExpenditureRepository extends JpaRepository<GoalExpenditure,Long>{
    
}
