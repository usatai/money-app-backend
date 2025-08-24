package com.example.money.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
            // String authHeader = request.getHeader("Authorization");
            String token = resolveToken(request);
            if (token != null) {
                try {
                    Claims claims = jwtUtil.parse(token).getBody();
                    String username = claims.getSubject();
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) claims.get("roles", List.class);
                    var auth = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            roles == null ? List.of() :
                                    roles.stream().map(SimpleGrantedAuthority::new).toList());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (JwtException e) {

                }

            }

        // if (authHeader != null && authHeader.startsWith("Bearer ")) {
        //     String token = authHeader.substring(7);
        //     try {
        //         String username = jwtUtil.validateTokenAndGetUsername(token);
        //         UsernamePasswordAuthenticationToken authentication =
        //             new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        //         SecurityContextHolder.getContext().setAuthentication(authentication);
        //     } catch (JwtException e) {
        //         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //         return;
        //     }
        // }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("access_token".equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }
    
}
