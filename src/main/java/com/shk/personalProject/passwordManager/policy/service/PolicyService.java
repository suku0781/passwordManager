package com.shk.personalProject.passwordManager.policy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shk.personalProject.passwordManager.agent.PasswordPolicyAgent;
import com.shk.personalProject.passwordManager.agent.PasswordPolicyTool;
import com.shk.personalProject.passwordManager.policy.dto.PolicyAnalysisResult;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChatLanguageModel chatLanguageModel;
    private final PasswordPolicyTool passwordPolicyTool;

    private static final String CACHE_PREFIX = "policy:";
    private static final long CACHE_TTL_HOURS = 24;

    // 매 호출마다 Tool이 연결된 Agent를 생성.
    private PasswordPolicyAgent buildAgent() {
        return AiServices.builder(PasswordPolicyAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(passwordPolicyTool)
                .build();
    }

    private PasswordPolicyAgent buildFallbackAgent() {
        return AiServices.builder(PasswordPolicyAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
    public PolicyAnalysisResult analyzePolicy(String siteUrl) {
        // 1. Redis 캐시 확인
        String cached = redisTemplate.opsForValue().get(CACHE_PREFIX + siteUrl);
        if (cached != null) {
            log.info("캐시 히트: {}", siteUrl);
            return parseResult(cached);
        }

        // 2. Jsoup 크롤링 시도
        log.info("Jsoup 크롤링 시도: {}", siteUrl);
        String analysisJson = null;

        try {
//            analysisJson = buildAgent().analyzePolicyFromText("이 웹사이트의 회원가입 시 아이디 비밀번호 제한 조건을 JSON형식에 맞게 가져워주세요." + siteUrl);
            analysisJson = buildAgent().analyzePolicyFromKnowledge(siteUrl);
            analysisJson = cleaningMd(analysisJson);

            if(isCrowlFailed(analysisJson)) throw new RuntimeException("크롤링 실패");
        } catch (Exception e) {
            log.warn("크롤링 실패. Gemini 지식 기반으로 전환. {}", siteUrl);
            analysisJson = buildFallbackAgent().analyzePolicyFromKnowledge(siteUrl  +" 사이트 비밀번호 정책");
            analysisJson = cleaningMd(analysisJson);
        }

        log.info("AI 분석 결과: {}", analysisJson);

        // 3. Redis 캐싱
        redisTemplate.opsForValue().set(
                CACHE_PREFIX + siteUrl,
                analysisJson,
                CACHE_TTL_HOURS,
                TimeUnit.HOURS
        );
        return parseResult(analysisJson);
    }

    // 실패 피드백으로 정책 보정
    public PolicyAnalysisResult refinePolicyByFeedback(String siteUrl, String errorMessage) {
        log.info("피드백 기반 정책 보정: {}, 오류: {}", siteUrl, errorMessage);

        String prompt = String.format("""
                사이트: %s
                비밀번호 입력 시 받은 오류 메시지: "%s"

                이 오류 메시지를 분석해서 실제 비밀번호 정책을 JSON으로 반환해주세요.
                """, siteUrl, errorMessage);

        String refined = buildAgent().analyzePolicyFromText(prompt);
        refined = cleaningMd(refined);

        // 캐시 갱신
        redisTemplate.opsForValue().set(
                CACHE_PREFIX + siteUrl,
                refined,
                CACHE_TTL_HOURS,
                TimeUnit.HOURS
        );

        return parseResult(refined);
    }

    private PolicyAnalysisResult parseResult(String json) {
        try {
            return objectMapper.readValue(cleaningMd(json), PolicyAnalysisResult.class);
        } catch (Exception e) {
            log.warn("JSON 파싱 실패, 기본값 반환: {}", e.getMessage());
            return PolicyAnalysisResult.defaultPolicy();
        }
    }

    // 마크다운 코드블럭 제거
    public String cleaningMd(String str) {
        return str.replaceAll("(?s)```json\\s*", "")
                .replaceAll("```", "")
                .trim();
    }

    // 크롤링 실패 여부 확인
    public boolean isCrowlFailed(String json) {
        return json.contains("크롤링 실패") || json.contains("찾을 수 없");
    }

}