package com.shk.personalProject.passwordManager.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface PasswordPolicyAgent {
    @SystemMessage("""
        당신은 웹사이트 비밀번호 정책 분석 전문가입니다.
        
        반드시 아래 순서로 진행하세요. 
        1. findSignupPageUrl 툴로 회원가입 페이지 URL을 먼저 찾으세요. 
        2. crawlPasswordPolicy 툴로 찾은 URL에서 비밀번호 정책 텍스트를 추출하세요. 
        3. 추출한 텍스트를 분석해서 아래 JSON 형식으로만 응답하세요. 
        
        다른 텍스트, 설명, 마크다운 코드블록은 절대 포함하지 마세요. 
        
        {
          "minLength": 숫자,
          "maxLength": 숫자 또는 null,
          "requireUppercase": true/false,
          "requireLowercase": true/false,
          "requireNumber": true/false,
          "requireSpecial": true/false,
          "allowedSpecialChars": "허용된 특수문자 문자열 또는 null",
          "notes": "기타 특이사항 또는 null"
        }
        
        정책 정보가 없으면 일반적인 기본값으로 응답하세요.
        """)
    String analyzePolicyFromText(@UserMessage String text);

    // 추가 - 크롤링 없이 Gemini 지식으로만
    @SystemMessage("""
        You are a website password policy expert.
        Answer only from your knowledge without crawling.
    
        Even if you don't know the exact policy, you MUST infer reasonable values
        based on the site type and scale. Never default everything to false or null.
        Portal/SNS/financial sites have high security requirements.
    
        Return ONLY valid JSON without any explanation or markdown.
    
        {
          "minLength": number,
          "maxLength": number or null,
          "requireUppercase": true/false,
          "requireLowercase": true/false,
          "requireNumber": true/false,1
          "requireSpecial": true/false,
          "allowedSpecialChars": "allowed special chars string or null",
          "notes": null
        }
        """)
    String analyzePolicyFromKnowledge(@UserMessage String siteName);
}