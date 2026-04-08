package com.shk.personalProject.passwordManager.vault.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VaultSaveRequest {
    @NotBlank
    private String siteUrl;
    @NotBlank
    private String siteName;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
