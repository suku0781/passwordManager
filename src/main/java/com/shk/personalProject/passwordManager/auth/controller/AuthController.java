package com.shk.personalProject.passwordManager.auth.controller;

import com.shk.personalProject.passwordManager.auth.dto.LoginRequest;
import com.shk.personalProject.passwordManager.auth.dto.SignupRequest;
import com.shk.personalProject.passwordManager.auth.dto.TokenResponse;
import com.shk.personalProject.passwordManager.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}