package com.example.money.controller;


import com.example.money.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import com.example.money.config.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/user")
public class UserController {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    private final String cookieDomain;
    private final boolean cookieSecure;
    private final String cookieSameSite;


    public UserController (
        JwtUtil jwtUtil,
        UserService userService,
        @Value("${security.cookie.domain}") String cookieDomain,
        @Value("${security.cookie.secure}") boolean cookieSecure,
        @Value("${security.cookie.sameSite}") String cookieSameSite
        ) 
        {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.cookieDomain = cookieDomain;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    //新規ユーザー登録
    @PostMapping("signup")
    public ResponseEntity<?> signup(@RequestBody @Validated UserForm userForm, BindingResult userBindingResult, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (userBindingResult.hasErrors()) {
            List<String> errorMessage = userBindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest().body(Map.of("errors",errorMessage));
        } else {
            userService.save(userForm);
            var userId = userService.getUserid(userForm);
            Integer userIdInt = userId.orElse(null);

            // セキュリティ JWT生成
            String access = jwtUtil.generateAccessToken(userForm.user_name(),List.of("USER"));
            String refresh = jwtUtil.generateRefreshToken(userForm.user_name());

            // Cookie発行
            ResponseCookie accessCookie = createAccessCookie(access);
            ResponseCookie refreshCookie = createRefreshCookie(refresh);

            // String token = jwtUtil.generateToken(userForm.user_name());

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE,refreshCookie.toString())
                .body(Map.of(
                    "message","User registered successfully", 
                    "userId", userIdInt,
                    "token",access
                ));
        }
    }

    //ログイン処理
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginForm loginForm, BindingResult loginBindingResult,HttpServletRequest request,
    HttpServletResponse response,RedirectAttributes redirectAttributes) {

        if (loginBindingResult.hasErrors()) {
            System.out.println("エラー");
            List<String> errorMessage = loginBindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();

            System.out.println(errorMessage);

            return ResponseEntity.badRequest().body(Map.of("errors",errorMessage));
        }

        // ユーザー識別情報取得
        var userId = userService.getLoginUserid(loginForm);
        Integer userIdInt = userId.orElse(null);

        // セキュリティ JWT生成
        String access = jwtUtil.generateAccessToken(loginForm.loginUser_name(),List.of("USER"));
        String refresh = jwtUtil.generateRefreshToken(loginForm.loginUser_name());

        // Cookie発行
        ResponseCookie accessCookie = createAccessCookie(access);
        ResponseCookie refreshCookie = createRefreshCookie(refresh);

        // String token = jwtUtil.generateToken(loginForm.loginUser_name());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(Map.of(
                "message","Login successful",
                "userId", userIdInt,
                "token",access
            ));
    }

    //ログイン処理
    @PostMapping("gestLogin")
    public ResponseEntity<?> gestLogin(@RequestBody @Validated GestLoginUserForm gestUserForm, BindingResult userBindingResult,HttpServletRequest request,
    HttpServletResponse response,HttpSession session,RedirectAttributes redirectAttributes) {

        if (userBindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(userBindingResult.getAllErrors());
        } else {
            userService.gestUserSave(gestUserForm);
            var userId = userService.getGestUserid(gestUserForm);
            Integer userIdInt = userId.orElse(null);

            // セキュリティ JWT生成
            String access = jwtUtil.generateAccessToken(gestUserForm.gestLoginUserName(),List.of("USER"));
            String refresh = jwtUtil.generateRefreshToken(gestUserForm.gestLoginUserName());

            // Cookie発行
            ResponseCookie accessCookie = createAccessCookie(access);
            ResponseCookie refreshCookie = createRefreshCookie(refresh);

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("message","User registered successfully",
                 "userId", userIdInt,
                 "token",access
                ));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token",required = false) String refreshToken) {
        if (refreshToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Claims claims;
        try {
            claims = jwtUtil.parse(refreshToken).getBody();
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String access = jwtUtil.generateAccessToken(claims.getSubject(), List.of("USER"));
        ResponseCookie accessCookie = ResponseCookie.from("access_token", access)
            .httpOnly(true).secure(cookieSecure).path("/")
            .domain(cookieDomain).sameSite(cookieSameSite)
            .maxAge(Duration.ofMinutes(jwtUtil.getAccessMinutes()))
            .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).body(Map.of("result","ok"));
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

    @GetMapping("logout")
    public ResponseEntity<?> userLogout() {
        ResponseCookie clear1 = ResponseCookie.from("access_token","").path("/").maxAge(0).build();
        ResponseCookie clear2 = ResponseCookie.from("refresh_token","").path("/").maxAge(0).build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clear1.toString())
            .header(HttpHeaders.SET_COOKIE, clear2.toString())
            .build();
    }
    

    // Cookie生成
    private ResponseCookie createAccessCookie(String access) {
        // Cookie発行
        ResponseCookie accessCookie = ResponseCookie.from("access_token",access)
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .domain(cookieDomain)
            .sameSite(cookieSameSite)
            .maxAge(Duration.ofMinutes(jwtUtil.getAccessMinutes()))
            .build();

        return accessCookie;
    }

    private ResponseCookie createRefreshCookie(String refresh) {
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refresh)
            .httpOnly(true)
            .secure(cookieSecure)
            .domain(cookieDomain)
            .path("/") // もしくは "/auth/refresh" に限定
            .sameSite(cookieSameSite)
            .maxAge(Duration.ofDays(jwtUtil.getRefreshDays()))
            .build();
        
        return refreshCookie;
    }
}
