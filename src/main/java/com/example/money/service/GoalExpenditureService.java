package com.example.money.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.money.controller.GoalExpenditureForm;
import com.example.money.model.GoalExpenditure;
import com.example.money.repository.GoalExpenditureRepository;

@Service
public class GoalExpenditureService {

    private final GoalExpenditureRepository goalExpenditureRepository;

    public GoalExpenditureService (GoalExpenditureRepository goalExpenditureRepository) {
        this.goalExpenditureRepository = goalExpenditureRepository;
    }

    public void save(GoalExpenditureForm goalExpenditureForm,Integer userIdInt) {
        goalExpenditureRepository.save(createGoalExpenditure(goalExpenditureForm,userIdInt));
    }

    private GoalExpenditure createGoalExpenditure(GoalExpenditureForm goalExpenditureForm,Integer userIdInt) {
        GoalExpenditure goalExpenditure = new GoalExpenditure();
        Date now = new Date();

        goalExpenditure.setGoal_expenditure(goalExpenditureForm.goal_expenditure());
        goalExpenditure.setUser_id(userIdInt);
        goalExpenditure.setCreated_at(now);
        goalExpenditure.setUpdated_at(now);

        return goalExpenditure;
    }
}
