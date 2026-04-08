package com.shk.personalProject.passwordManager.vault.service;

import com.shk.personalProject.passwordManager.domain.User;
import com.shk.personalProject.passwordManager.domain.UserRepository;
import com.shk.personalProject.passwordManager.domain.VaultEntry;
import com.shk.personalProject.passwordManager.domain.VaultEntryRepository;
import com.shk.personalProject.passwordManager.vault.crypto.AesEncryptionService;
import com.shk.personalProject.passwordManager.vault.dto.VaultResponse;
import com.shk.personalProject.passwordManager.vault.dto.VaultSaveRequest;
import com.shk.personalProject.passwordManager.vault.dto.VaultSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VaultService {
    private final VaultEntryRepository vaultEntryRepository;
    private final UserRepository userRepository;
    private final AesEncryptionService aesEncryptionService;

    @Transactional
    public void save(String email, VaultSaveRequest request){
        // 1. 이메일로 사용자 찾기. 없으면 exception 던지기
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 보안을 위해서 아이디, 비밀번호를 암호화(복호화가 가능한 AES 방식으로)
        String encryptedUserName =  aesEncryptionService.encrypt(request.getUsername());
        String encryptedPassword = aesEncryptionService.encrypt(request.getPassword());

        // 3. 이미 이 사이트 계정 정보가 등록되어있는지 확인
        if(vaultEntryRepository.existsByUserIdAndSiteUrl(user.getId(), request.getSiteUrl())){
            // 이미 있으면 기존 정보를 가져와서 새 값으로 업데이트
            VaultEntry entry = vaultEntryRepository.findByUserIdAndSiteUrl(user.getId(), request.getSiteUrl()).orElseThrow();

            // JPA는 메서드가 끝날 때 변경 사항을 감지해서 자동으로 DB에 반영함. (Dirty Checking)
            entry.updateCredentials(encryptedUserName, encryptedPassword);
        } else {
            // 없으면 빌더 패턴을 사용해서 새로운 VaultEntity 생성.
            VaultEntry entry = VaultEntry.builder()
                    .user(user)
                    .siteUrl(request.getSiteUrl())
                    .siteName(request.getSiteName())
                    .encryptedUsername(encryptedUserName)
                    .encryptedPassword(encryptedPassword)
                    .build();

            // DB에 새 데이터 저장.
            vaultEntryRepository.save(entry);
        }
    }

    public VaultResponse get(String email, String siteUrl) {
        // 1. 사용자 확인
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 해당 사용자의 특정 사이트 정보를 DB에서 가져오기.
        VaultEntry entry = vaultEntryRepository.findByUserIdAndSiteUrl(user.getId(), siteUrl).orElseThrow(() -> new IllegalArgumentException("저장된 계정 정보가 없습니다."));

        // 3. DB에 암호화된 정보를 다시 복호화.
        String userName = aesEncryptionService.decrypt(entry.getEncryptedUsername());
        String password = aesEncryptionService.decrypt(entry.getEncryptedPassword());

        // 4. 반환.
        return new VaultResponse(entry, userName, password);
    }

    public List<VaultSummary> getList(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return vaultEntryRepository.findAllByUserId(user.getId())
                .stream()
                .map(VaultSummary::new)
                .toList();
    }
}

