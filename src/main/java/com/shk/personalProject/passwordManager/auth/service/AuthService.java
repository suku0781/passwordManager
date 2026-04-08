package com.shk.personalProject.passwordManager.auth.service;

import com.shk.personalProject.passwordManager.auth.dto.LoginRequest;
import com.shk.personalProject.passwordManager.auth.dto.SignupRequest;
import com.shk.personalProject.passwordManager.auth.dto.TokenResponse;
import com.shk.personalProject.passwordManager.auth.jwt.JwtProvider;
import com.shk.personalProject.passwordManager.domain.User;
import com.shk.personalProject.passwordManager.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        // Spring Security가 인증 처리 (비밀번호 검증 포함)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtProvider.generateToken(request.getEmail());
        return TokenResponse.of(token);
    }
}