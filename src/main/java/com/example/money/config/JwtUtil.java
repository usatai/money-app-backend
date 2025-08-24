package com.example.money.config;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private final Key key;
    private final long accessMinutes;
    private final long refreshDays;

    public JwtUtil ( 
        @Value("${security.jwt.secretBase64}") String secretBase64,
        @Value("${security.jwt.accessMinutes}") long accessMinutes,
        @Value("${security.jwt.refreshDays}") long refreshDays
    ) {
        byte[] secret = Base64.getDecoder().decode(secretBase64);
        if (secret.length < 32) {
            throw new IllegalArgumentException();
        }
        this.key = Keys.hmacShaKeyFor(secret);
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
    }

    public String generateAccessToken(String username,Collection<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(Duration.ofMinutes(accessMinutes))))
            .signWith(key,SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(Duration.ofDays(refreshDays))))
            .signWith(key,SignatureAlgorithm.HS256)
            .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);
    }

    public long getAccessMinutes() { return accessMinutes; }
    public long getRefreshDays() { return refreshDays; }
}
