package com.shk.personalProject.passwordManager.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "vault_entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class VaultEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String siteUrl;

    @Column(nullable = false)
    private String siteName;

    @Column(nullable = false)
    private String encryptedUsername;

    @Column(nullable = false)
    private String encryptedPassword;


    private LocalDateTime passwordChangedAt; // 비밀번호 만료일 추적 (추후 알림 기능용)

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public VaultEntry(User user, String siteUrl, String siteName, String encryptedUsername, String encryptedPassword) {
        this.user = user;
        this.siteUrl = siteUrl;
        this.siteName = siteName;
        this.encryptedUsername = encryptedUsername;
        this.encryptedPassword = encryptedPassword;
        this.passwordChangedAt = LocalDateTime.now();
    }

    public void updateCredentials(String encryptedUsername, String encryptedPassword) {
        this.encryptedUsername = encryptedUsername;
        this.encryptedPassword = encryptedPassword;
        this.passwordChangedAt = LocalDateTime.now();
    }

}
