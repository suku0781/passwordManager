package com.shk.personalProject.passwordManager.vault.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordGenerateRequest {
    @NotBlank
    private String siteUrl;
}