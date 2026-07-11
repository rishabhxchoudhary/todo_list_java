package com.codenzyme.todolist.service;

import com.codenzyme.todolist.dto.RotateResponse;
import com.codenzyme.todolist.entity.AppUser;
import com.codenzyme.todolist.entity.RefreshToken;
import com.codenzyme.todolist.exception.InvalidRefreshTokenException;
import com.codenzyme.todolist.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Duration refreshTokenValidity = Duration.ofDays(10);
    private final int randomByteLength = 32;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String buildRefreshToken(AppUser user, UUID familyId) {
        String rawToken = generateRawToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(hashToken(rawToken));
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plus(refreshTokenValidity));
        refreshToken.setRevoked(false);
        refreshToken.setFamilyId(familyId);
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    @Transactional
    public void revokeAllTokensForUser(AppUser user) {
        refreshTokenRepository.revokeAllForUser(user);
    }

    public String createRefreshToken(AppUser user) {
        return buildRefreshToken(user, UUID.randomUUID());
    }

    @Transactional
    public RotateResponse rotate(String rawTokenFromClient) {
        String hash = hashToken(rawTokenFromClient);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(hash);
        if (refreshToken.isEmpty()) {
            throw new InvalidRefreshTokenException("RefreshToken not found");
        }
        RefreshToken token = refreshToken.get();
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("Token expired");
        }
        if (token.isRevoked()) {
            log.warn("Refresh token reuse detected — revoking family {}", token.getFamilyId());
            refreshTokenRepository.revokeFamily(token.getFamilyId());
            throw new InvalidRefreshTokenException("Token revoked");
        }
        token.setRevoked(true);
        refreshTokenRepository.save(token);
        return new RotateResponse(token.getUser(),buildRefreshToken(token.getUser(), token.getFamilyId()));
    }

    private String generateRawToken() {
        byte[] randomBytes = new byte[randomByteLength];        // 32 bytes = 256 bits
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
