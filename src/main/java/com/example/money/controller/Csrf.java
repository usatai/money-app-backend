package com.example.money.controller; // あなたのパッケージ名に合わせてください

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
        
        // Spring Securityが生成したCSRFトークンを取得
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            // ResponseCookieビルダーを使って、手動でクッキーを生成
            ResponseCookie cookie = ResponseCookie.from(csrfToken.getHeaderName(), csrfToken.getToken())
                    .path("/")
                    .secure(true)
                    .sameSite("None")
                    .httpOnly(false)
                    .build();
            
            // レスポンスヘッダーにSet-Cookieを追加
            response.addHeader("Set-Cookie", cookie.toString());
            logger.info("Manually added XSRF-TOKEN cookie to the response.");
        } else {
            logger.warn("CSRF Token not found in request attribute.");
        }

        // フロントエンドのデバッグ用にトークン情報をボディで返す
        return csrfToken;
    }
}