package com.example.money.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Csrf {
    /**
     * フロントエンドがCSRFトークンを取得するためのエンドポイント。
     * このエンドポイントにアクセスすることで、Spring SecurityのCsrfFilterが
     * トークンを生成し、Cookieにセットしてレスポンスを返す。
     *
     * @param token CsrfFilterによってリクエスト属性に設定されたトークン
     * @return 現在のCSRFトークン情報（デバッグ用に返すことも可能）
     */
    @GetMapping("/api/user/csrf")
    public CsrfToken getCsrfToken(CsrfToken token) {
        // このメソッドが呼び出されるだけで、CsrfFilterの働きにより
        // XSRF-TOKENクッキーがレスポンスに自動的に付与される。
        // ボディでトークン情報を返す必要は必ずしもないが、デバッグのために返している。
        return token;
    }
}
