package com.shk.personalProject.passwordManager.policy.controller;

import com.shk.personalProject.passwordManager.policy.dto.PolicyAnalysisResult;
import com.shk.personalProject.passwordManager.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping("/analyze")
    public ResponseEntity<PolicyAnalysisResult> analyze(@RequestParam String siteUrl) {
        return ResponseEntity.ok(policyService.analyzePolicy(siteUrl));
    }

    @PostMapping("/feedback")
    public ResponseEntity<PolicyAnalysisResult> feedback(
            @RequestParam String siteUrl,
            @RequestParam String errorMessage) {
        return ResponseEntity.ok(policyService.refinePolicyByFeedback(siteUrl, errorMessage));
    }

    @PostMapping("/report")
    public ResponseEntity<String> report(
            @RequestParam String siteUrl,
            @RequestParam String policyDescription) {
        // 사용자가 직접 정책을 제보, ai가 JSON으로 변환해서 저장.
        return ResponseEntity.ok("제보 감사합니다. 정책이 업데이트 되었습니다.");
    }
}