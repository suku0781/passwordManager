package com.shk.personalProject.passwordManager.vault.dto;

import com.shk.personalProject.passwordManager.domain.VaultEntry;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VaultResponse {
    private final Long id;
    private final String siteUrl;
    private final String siteName;
    private final String userName;
    private final String password;
    private final LocalDateTime passwordChangedAt;

    public VaultResponse(VaultEntry vaultEntry, String username, String password) {
        this.id = vaultEntry.getId();
        this.siteUrl = vaultEntry.getSiteUrl();
        this.siteName = vaultEntry.getSiteName();
        this.userName = username;
        this.password = password;
        this.passwordChangedAt = vaultEntry.getPasswordChangedAt();
    }
}
