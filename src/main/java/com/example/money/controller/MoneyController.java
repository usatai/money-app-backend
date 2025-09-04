package com.example.money.controller;

import com.example.money.service.MoneyService;
import com.example.money.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class MoneyController {

    private final MoneyService moneyService;
    private final UserService userService;

    public MoneyController(MoneyService moneyService, UserService userService) {
        this.moneyService = moneyService;
        this.userService = userService;
    }

    @PostMapping("/money")
    public ResponseEntity<?> moneyInput(@RequestBody @Validated MoneyForm moneyForm, BindingResult moneyBindingResult, Principal principal){
        if (moneyBindingResult.hasErrors()){
            List<String> errorMessage = moneyBindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest().body(Map.of("errors",errorMessage));
        }
    
        String username = principal.getName(); // トークン内のユーザー名

        Integer userIdInt = userService.getUserIdByUsername(username)
            .orElseThrow(() -> new RuntimeException("ユーザーIDが見つかりません: " + username));

        
        moneyService.moneyInput(moneyForm,userIdInt);

        return ResponseEntity.ok(Map.of("successMessage","登録完了"));
    }

}
