package com.example.money.controller;

import com.example.money.service.MoneyService;
import com.example.money.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    
        String username = authentication.getName(); // トークン内のユーザー名
        Integer userIdInt = userService.getUserIdByUsername(username)
            .orElseThrow(() -> new RuntimeException("ユーザーIDが見つかりません: " + username));

        moneyService.moneyInput(moneyForm,userIdInt);

        return ResponseEntity.ok(Map.of("successMessage","登録完了"));
    }

}
