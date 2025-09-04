package com.example.money.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;

public class CsrfDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ログインのPOSTリクエストの時だけログを出すように絞り込む
        if ("POST".equalsIgnoreCase(request.getMethod()) && "/api/user/login".equals(request.getRequestURI())) {
            
            System.out.println("\n--- CSRF DEBUGGER ---");
            System.out.println("Request URI: " + request.getRequestURI());

            // 1. ヘッダーから X-XSRF-TOKEN を取得
            String headerToken = request.getHeader("X-XSRF-TOKEN");
            System.out.println("Header 'X-XSRF-TOKEN': " + (headerToken != null ? headerToken : "NOT FOUND"));

            // 2. Cookieから XSRF-TOKEN を取得
            String cookieToken = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                cookieToken = Arrays.stream(cookies)
                        .filter(c -> "XSRF-TOKEN".equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse("NOT FOUND IN COOKIES");
            }
            System.out.println("Cookie 'XSRF-TOKEN': " + (cookieToken != null ? cookieToken : "NO COOKIES AT ALL"));
            System.out.println("--- END CSRF DEBUGGER ---\n");
        }

        filterChain.doFilter(request, response);
    }
}