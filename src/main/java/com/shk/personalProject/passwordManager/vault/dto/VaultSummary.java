package com.shk.personalProject.passwordManager.vault.dto;

import com.shk.personalProject.passwordManager.domain.VaultEntry;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VaultSummary {
    private final Long id;
    private final String siteUrl;
    private final String siteName;
    private final LocalDateTime passwordChangedAt;

    public VaultSummary(VaultEntry entry){
        this.id = entry.getId();
        this.siteUrl = entry.getSiteUrl();
        this.siteName = entry.getSiteName();
        this.passwordChangedAt = entry.getPasswordChangedAt();
    }

}
