package com.example.money.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Csrf {

    private static final Logger logger = LoggerFactory.getLogger(Csrf.class);

    @GetMapping("/api/user/csrf")
    public CsrfToken getCsrfToken(HttpServletRequest request, HttpServletResponse response) {

        // --- 手動でクッキーを追加する最終テスト ---
        ResponseCookie testCookie = ResponseCookie.from("TEST-COOKIE", "hello-from-render")
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(false)
                .maxAge(3600)
                .build();
        
        response.addHeader("Set-Cookie", testCookie.toString());
        logger.info("[DEBUG] Manually added TEST-COOKIE header.");
        // --- テスト終了 ---

        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }
}