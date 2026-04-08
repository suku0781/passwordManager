package com.shk.personalProject.passwordManager.vault.controller;

import com.shk.personalProject.passwordManager.domain.User;
import com.shk.personalProject.passwordManager.domain.VaultEntry;
import com.shk.personalProject.passwordManager.domain.VaultEntryRepository;
import com.shk.personalProject.passwordManager.vault.dto.VaultResponse;
import com.shk.personalProject.passwordManager.vault.dto.VaultSaveRequest;
import com.shk.personalProject.passwordManager.vault.dto.VaultSummary;
import com.shk.personalProject.passwordManager.vault.service.VaultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Json데이터를 주고 받는 입구 라고 선언.
@RequestMapping("/api/vault")
@RequiredArgsConstructor // final로 선언된 VaultService를 스프링이 자동으로 연결(주입).
public class VaultController {
    private final VaultService vaultService;

    /**
     * @AuthenticationPrincipal : 인증된 사용자 정보를 쉽게 주입받을 수 있음. 인증되지 않은 사용자가 접근할 수 있는 경로에서는 사용할 수 없음.
     * @Valid : @RequestBody 어노테이션 옆에 작성 시 RequestBody로 들엉는 객체에 대한 검증을 수행. 검증의 세부사항은 객체 안에 정의해 두어야 함.
     */

    @PostMapping
    public ResponseEntity<String> save(@AuthenticationPrincipal UserDetails userDetails,
                                       @Valid @RequestBody VaultSaveRequest request) {
        vaultService.save(userDetails.getUsername(), request);
        return ResponseEntity.ok("저장 완료");
    }

    // 단건 조회 요청(/api/vault?siteUrl=naver.com)
    @GetMapping
    public ResponseEntity<VaultResponse> get(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam String siteUrl) {
        return ResponseEntity.ok(vaultService.get(userDetails.getUsername(), siteUrl));
    }

    @GetMapping("/list")
    public ResponseEntity<List<VaultSummary>> getList(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(vaultService.getList(userDetails.getUsername()));
    }
}
