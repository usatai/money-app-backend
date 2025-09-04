package com.example.money.controller;

import com.example.money.service.LabelService;
import com.example.money.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class LabelController {

    @Autowired
    LabelService labelService;

    @Autowired
    UserService userService;

    @PostMapping("/input")
    public ResponseEntity<?> input(@RequestBody @Validated LabelForm labelForm, BindingResult bindingResult,Model model, RedirectAttributes redirectAttributes,Principal principal){
        if(bindingResult.hasErrors()){
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(Map.of("errors",errorMessage));
        }
    
        String username = principal.getName(); // トークン内のユーザー名
        Integer userIdInt = userService.getUserIdByUsername(username)
            .orElseThrow(() -> new RuntimeException("ユーザーIDが見つかりません: " + username));
        System.out.println("ユーザーID" + userIdInt);
        YearMonth yearMonth = labelForm.currentDate();

        labelService.input(labelForm,userIdInt,yearMonth);

        return ResponseEntity.ok(Map.of("message","登録完了"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteMoneyLabel(@RequestBody @Validated DeleteForm deleteForm, BindingResult bindingResult,RedirectAttributes redirectAttributes, HttpSession session){
        if(bindingResult.hasErrors()){
            List<String> errorMessage = bindingResult.getAllErrors().stream()
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
        YearMonth yearMonth = deleteForm.currentDate();

        String yearMonthSt = yearMonth.toString();

        labelService.deleteLabel(userIdInt,deleteForm,yearMonthSt);

        return ResponseEntity.ok(Map.of("message","削除しました。"));
    }

}
