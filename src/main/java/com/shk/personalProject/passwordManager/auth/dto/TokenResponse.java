package com.shk.personalProject.passwordManager.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public static TokenResponse of(String token){
        return new TokenResponse(token, "Bearer");
    }
}
