package com.shk.personalProject.passwordManager.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VaultEntryRepository extends JpaRepository<VaultEntry, Long> {
    List<VaultEntry> findAllByUserId(Long userId);
    Optional<VaultEntry> findByUserIdAndSiteUrl(Long userId, String siteUrl);
    boolean existsByUserIdAndSiteUrl(Long userId, String siteUrl);
}
