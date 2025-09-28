package com.example.money.config; // あなたのパッケージ名に合わせてください

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResponseHeaderDebugFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseHeaderDebugFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 次のフィルター（最終的にはコントローラー）を実行
        chain.doFilter(request, response);

        // レスポンスが返ってきた後でヘッダーをチェック
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // 特にSet-Cookieヘッダーの存在を確認
        if (httpServletResponse.containsHeader("Set-Cookie")) {
            logger.info("[DEBUG] Set-Cookie header FOUND! Value: {}", httpServletResponse.getHeader("Set-Cookie"));
        } else {
            logger.warn("[DEBUG] Set-Cookie header NOT FOUND.");
        }
    }
}