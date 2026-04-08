package com.shk.personalProject.passwordManager.policy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// policy/dto/PolicyAnalysisResult.java
@Data
@Builder
@NoArgsConstructor // no, all 은 역 직력화를 위함
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyAnalysisResult {
    private int minLength;
    private Integer maxLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireNumber;
    private boolean requireSpecial;
    private String allowedSpecialChars;
    private String notes;

    public static PolicyAnalysisResult defaultPolicy() {
        return PolicyAnalysisResult.builder()
                .minLength(8)
                .requireUppercase(true)
                .requireLowercase(true)
                .requireNumber(true)
                .requireSpecial(true)
                .allowedSpecialChars("!@#$%^&*")
                .notes("기본 정책 적용")
                .build();
    }
}
