package com.shk.personalProject.passwordManager.policy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shk.personalProject.passwordManager.policy.dto.PolicyAnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyDataInitializr implements ApplicationRunner {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX = "policy:";
    private static final long CACHE_TTL_HOURS = 720; // 30일

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedPolicy("https://naver.com", PolicyAnalysisResult.builder()
                .minLength(6).maxLength(16)
                .requireUppercase(false).requireLowercase(true)
                .requireNumber(true).requireSpecial(false)
                .allowedSpecialChars(null)
                .notes("영문 소문자, 숫자 조합 6~16자").build());

        seedPolicy("https://kakao.com", PolicyAnalysisResult.builder()
                .minLength(8).maxLength(32)
                .requireUppercase(false).requireLowercase(true)
                .requireNumber(true).requireSpecial(true)
                .allowedSpecialChars("!@#$%^&*")
                .notes("8~32자, 숫자+특수문자 포함").build());

        seedPolicy("https://google.com", PolicyAnalysisResult.builder()
                .minLength(8).maxLength(null)
                .requireUppercase(false).requireLowercase(false)
                .requireNumber(false).requireSpecial(false)
                .allowedSpecialChars(null)
                .notes("8자 이상").build());

        seedPolicy("https://github.com", PolicyAnalysisResult.builder()
                .minLength(15).maxLength(null)
                .requireUppercase(false).requireLowercase(false)
                .requireNumber(false).requireSpecial(false)
                .allowedSpecialChars(null)
                .notes("15자 이상 또는 숫자+소문자 포함 8자 이상").build());

        seedPolicy("https://instagram.com", PolicyAnalysisResult.builder()
                .minLength(6).maxLength(null)
                .requireUppercase(false).requireLowercase(false)
                .requireNumber(false).requireSpecial(false)
                .allowedSpecialChars(null)
                .notes("6자 이상").build());

        log.info("주요 사이트 비밀번호 정책 초기 데이터 로딩 완료");
    }

    private void seedPolicy(String url, PolicyAnalysisResult policy) throws Exception {
        String key = CACHE_PREFIX + url;
        // 이미 캐시가 있으면 덮어쓰지 않음
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(policy),
                    CACHE_TTL_HOURS,
                    TimeUnit.HOURS
            );

            log.info("정책 초기 데이터 저장: {}", url);
        }
    }
}