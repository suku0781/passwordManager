package com.shk.personalProject.passwordManager.vault.dto;

import com.shk.personalProject.passwordManager.policy.dto.PolicyAnalysisResult;
import lombok.Getter;

@Getter
public class PasswordGenerateResponse {
    private final String siteUrl;
    private final String generatedPassword;
    private final PolicyAnalysisResult appliedPolicy;

    public PasswordGenerateResponse(String siteUrl, String password, PolicyAnalysisResult policy) {
        this.siteUrl = siteUrl;
        this.generatedPassword = password;
        this.appliedPolicy = policy;
    }
}