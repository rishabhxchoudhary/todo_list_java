package com.codenzyme.todolist.service;

import com.codenzyme.todolist.entity.AppUser;
import com.codenzyme.todolist.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final UserRepository userRepository;

    public JwtService(@Value("${jwt.secret}") String secret, UserRepository userRepository) {
        this.secretKey = this.getSecretKey(secret);
        this.userRepository = userRepository;
    }

    private long jwtExpiration = 15 * 60 * 1000;

    private SecretKey getSecretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(AppUser appUser) {
        return Jwts.builder()
                .subject(appUser.getUsername())
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
        Optional<AppUser> user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new UsernameNotFoundException(username);
        return user.get();
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
