package com.example.money.controller;

import com.example.money.service.MoneyService;
import com.example.money.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class MoneyController {

    @Autowired
    MoneyService moneyService;

    @Autowired
    UserService userService;

    @PostMapping("/money")
    public ResponseEntity<?> moneyInput(@RequestBody @Validated MoneyForm moneyForm, BindingResult moneyBindingResult, HttpSession session){
        if (moneyBindingResult.hasErrors()){
            List<String> errorMessage = moneyBindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest().body(Map.of("errors",errorMessage));
        }

        Integer userIdInt = (Integer)session.getAttribute("userIdInt");
        
        // セッションチェック
        if(userIdInt == null){
            return ResponseEntity.badRequest().body(Map.of("errors","ログインが必要です。"));
        }

        moneyService.moneyInput(moneyForm,userIdInt);

        return ResponseEntity.ok(Map.of("successMessage","登録"));
    }

}
