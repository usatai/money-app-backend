package com.example.money.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.money.service.GoalExpenditureService;
import com.example.money.service.UserService;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/user")
public class GoalExpenditureController {

    private final GoalExpenditureService goalExpenditureService;
    private final UserService userService;

    public GoalExpenditureController(GoalExpenditureService goalExpenditureService,UserService userService) {
        this.goalExpenditureService = goalExpenditureService;
        this.userService = userService;
    }

    @PostMapping("/goal_expenditure")
    public ResponseEntity<?> goal_expenditure(@RequestBody GoalExpenditureForm goalExpenditureForm,Principal principal) {
        String userName = principal.getName();
        Integer userIdInt = userService.getUserIdByUsername(userName)
            .orElseThrow(() -> new RuntimeException("ユーザーIDが見つかりません: " + userName));
        
        goalExpenditureService.save(goalExpenditureForm,userIdInt);




        return ResponseEntity.ok(Map.of("message","data save successfully"));
    }
    
    
    
}
