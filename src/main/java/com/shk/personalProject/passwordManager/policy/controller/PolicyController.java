package com.shk.personalProject.passwordManager.policy.controller;

import com.shk.personalProject.passwordManager.policy.dto.PolicyAnalysisResult;
import com.shk.personalProject.passwordManager.policy.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "비밀번호 정책", description = "AI 기반 사이트 정책 분석")
@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @Operation(summary = "정책 분석", description = "URL 입력 시 AI가 회원가입 페이지를 탐색해서 비밀번호 정책을 JSON으로 반환")
    @GetMapping("/analyze")
    public ResponseEntity<PolicyAnalysisResult> analyze(@RequestParam String siteUrl) {
        return ResponseEntity.ok(policyService.analyzePolicy(siteUrl));
    }

    @Operation(summary = "피드백 기반 정책 보정", description = "비밀번호 오류 메시지를 입력하면 AI 가 정책을 보정")
    @PostMapping("/feedback")
    public ResponseEntity<PolicyAnalysisResult> feedback(
            @RequestParam String siteUrl,
            @RequestParam String errorMessage) {
        return ResponseEntity.ok(policyService.refinePolicyByFeedback(siteUrl, errorMessage));
    }

    @Operation(summary = "사용자 직접 제보")
    @PostMapping("/report")
    public ResponseEntity<String> report(
            @RequestParam String siteUrl,
            @RequestParam String policyDescription) {
        // 사용자가 직접 정책을 제보, ai가 JSON으로 변환해서 저장.
        return ResponseEntity.ok("제보 감사합니다. 정책이 업데이트 되었습니다.");
    }
}