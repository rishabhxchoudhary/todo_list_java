package com.codenzyme.todolist.service;

import com.codenzyme.todolist.dto.LoginRequest;
import com.codenzyme.todolist.dto.LoginResponse;
import com.codenzyme.todolist.dto.SignupRequest;
import com.codenzyme.todolist.dto.UserResponse;
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

import java.util.Optional;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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

    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        AppUser appUser = principal.getAppUser();
        String token = jwtService.generateAccessToken(appUser);
        return new LoginResponse(token);
    }

}
