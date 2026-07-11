package com.codenzyme.todolist.service;

import com.codenzyme.todolist.dto.*;
import com.codenzyme.todolist.entity.AppUser;
import com.codenzyme.todolist.entity.Tier;
import com.codenzyme.todolist.exception.UsernameTakenException;
import com.codenzyme.todolist.repository.UserRepository;
import com.codenzyme.todolist.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    public UserResponse signup(SignupRequest request) {
        Optional<AppUser> user = userRepository.findByUsername(request.username());
        if (user.isPresent()) {
            throw new UsernameTakenException("Username already taken");
        }
        AppUser appUser = new AppUser();
        appUser.setUsername(request.username());
        appUser.setTier(Tier.FREE);
        appUser.setPasswordHash(this.passwordEncoder.encode(request.password()));
        userRepository.save(appUser);
        return new UserResponse(request.username());
    }

    public TokenResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        AppUser appUser = principal.getAppUser();
        String token = jwtService.generateAccessToken(appUser);
        return new TokenResponse(token, refreshTokenService.createRefreshToken(appUser));
    }

    public TokenResponse refresh(TokenRequest tokenRequest) {
        RotateResponse newRefreshTokenResponse = refreshTokenService.rotate(tokenRequest.refreshToken());
        String accessToken = jwtService.generateAccessToken(newRefreshTokenResponse.appUser());
        return new TokenResponse(accessToken,newRefreshTokenResponse.token());
    }

    @Transactional
    public void logout(AppUser appUser) {
        refreshTokenService.revokeAllTokensForUser(appUser);
    }
}
