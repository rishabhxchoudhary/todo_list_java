package com.codenzyme.todolist.service;

import com.codenzyme.todolist.entity.AppUser;
import com.codenzyme.todolist.entity.Tier;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = this.getSecretKey(secret);
    }

    private long jwtExpiration = 15 * 60 * 1000;

    private SecretKey getSecretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(AppUser appUser) {
        return Jwts.builder()
                .subject(appUser.getUsername())
                .claim("tier", appUser.getTier().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseAccessToken(String accessToken) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(accessToken).getPayload();
    }

    public String extractUsername(String token) {
        return this.parseAccessToken(token).getSubject();
    }

    public AppUser extractUser(String token) {
        String username = extractUsername(token);
        String tier = this.parseAccessToken(token).get("tier").toString();
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setTier(Tier.valueOf(tier));
        return appUser;
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

}
