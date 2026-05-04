package com.shk.personalProject.passwordManager.vault.service;

import com.shk.personalProject.passwordManager.policy.dto.PolicyAnalysisResult;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PasswordGeneratorService {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String DEFAULT_SPECIAL = "!@#$%^&*";

    private final SecureRandom random = new SecureRandom();

    public String generate(PolicyAnalysisResult policy) {
        List<Character> required = new ArrayList<>();
        StringBuilder pool = new StringBuilder();

        // 필수 문자 각 1개씩 먼저 추가
        if (policy.isRequireLowercase()) {
            required.add(randomChar(LOWERCASE));
            pool.append(LOWERCASE);
        }
        if (policy.isRequireUppercase()) {
            required.add(randomChar(UPPERCASE));
            pool.append(UPPERCASE);
        }
        if (policy.isRequireNumber()) {
            required.add(randomChar(NUMBERS));
            pool.append(NUMBERS);
        }
        if (policy.isRequireSpecial()) {
            String specialChars = (policy.getAllowedSpecialChars() != null && !policy.getAllowedSpecialChars().isBlank())
                    ? policy.getAllowedSpecialChars()
                    : DEFAULT_SPECIAL;
            required.add(randomChar(specialChars));
            pool.append(specialChars);
        }

        // pool이 비어있으면 기본값
        if (pool.isEmpty()) {
            pool.append(LOWERCASE).append(NUMBERS);
        }

        // 목표 길이 결정 (minLength + 여유 4자)
        int targetLength = Math.max(policy.getMinLength() + 4, required.size());
        if (policy.getMaxLength() != null) {
            targetLength = Math.min(targetLength, policy.getMaxLength());
        }
        targetLength = Math.max(targetLength, policy.getMinLength());

        // 나머지 자리 채우기
        while (required.size() < targetLength) {
            required.add(randomChar(pool.toString()));
        }

        // 순서 섞기 (필수 문자가 앞에 몰리지 않게)
        Collections.shuffle(required, random);

        StringBuilder result = new StringBuilder();
        for (char c : required) {
            result.append(c);
        }

        return result.toString();
    }

    private char randomChar(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }
}