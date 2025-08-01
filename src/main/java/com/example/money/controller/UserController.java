package com.example.money.controller;

import com.example.money.service.LabelService;
import com.example.money.service.MoneyService;
import com.example.money.service.UserService;
import com.example.money.config.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
//@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    MoneyService moneyService;

    @Autowired
    LabelService labelService;

    @Autowired
    JwtUtil jwtUtil;

    //新規ユーザー登録
    @PostMapping("signup")
    public ResponseEntity<?> signup(@RequestBody @Validated UserForm userForm, BindingResult userBindingResult, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (userBindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(userBindingResult.getAllErrors());
        } else {
            userService.save(userForm);
            var userId = userService.getUserid(userForm);
            Integer userIdInt = userId.orElse(null);

            session.setAttribute("userIdInt", userIdInt);

            return ResponseEntity.ok(Map.of("message","User registered successfully", "userId", userIdInt));
        }
    }

    //ログイン処理
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginForm loginForm, BindingResult loginBindingResult,HttpServletRequest request,
    HttpServletResponse response,HttpSession session,RedirectAttributes redirectAttributes) {

        if (loginBindingResult.hasErrors()) {
            System.out.println("エラー");
            List<String> errorMessage = loginBindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest().body(Map.of("errors",errorMessage));
        }

        //ユーザー識別情報取得
        var userId = userService.getLoginUserid(loginForm);
        System.out.println(userId);
        Integer userIdInt = userId.orElse(null);

        session.setAttribute("userIdInt", userIdInt);

        String token = jwtUtil.generateToken(loginForm.loginUser_name());

        return ResponseEntity.ok(Map.of("message","Login successful","userId", userIdInt,"token",token));
    }

    @GetMapping("check-auth")
    public ResponseEntity<?> checkAuth(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 認証されていない、または匿名ユーザーである場合
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // 認証済みであればOKを返す（必要ならユーザー情報も）
        return ResponseEntity.ok("Token valid");
    }
}
