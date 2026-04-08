package com.shk.personalProject.passwordManager.agent;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class PasswordPolicyTool {
    private final List<String> PW_KEYWARDS = Arrays.asList("비밀번호", "password", "특수문자", "자리", "숫자", "영문", "대문자", "소문자", "자 이상");
    private final List<String> SIGNUP_KEYWARDS = Arrays.asList("회원가입", "sign up", "signup", "register", "join", "create account");

    @Tool("웹사이트 URL에서 비밀번호 정책 관련 텍스트를 크롤링합니다")
    public String crawlPasswordPolicy(@P("분석할 웹사이트 URL") String url) {
        try {
            StringBuilder sb = new StringBuilder();
            Document doc = getDocument(url);

            // 비밀번호 관련 키워드가 포함된 요소 우선 추출
            doc.select("p, span, li, div, label, small")
                    .stream()
                    .map(Element::text)
                    .map(String::toLowerCase)
                    .filter(text -> PW_KEYWARDS.stream().anyMatch(text::contains))
                    .distinct()
                    .forEach(t -> sb.append(t).append("\n"));

            // 관련 텍스트가 없으면 전체 body 텍스트 일부
            if (sb.isEmpty()) {
                String bodyText = doc.body().text();
                return bodyText.length() > 2000 ? bodyText.substring(0, 2000) : bodyText;
            }

            String result = sb.toString();
            return result.length() > 3000 ? result.substring(0, 3000) : result;

        } catch (Exception e) {
            log.warn("크롤링 실패: {}", e.getMessage());
            return "크롤링 실패 - 기본 정책을 적용합니다: " + e.getMessage();
        }
    }

    @Tool("웹사이트 메인 URL에서 회원가입 페이지 링크를 찾습니다. ")
    public String findSignupPageUrl(@P("웹사이트 메인 URL") String url) {
        try{
            Document doc = getDocument(url);

            return doc.select("a")
                    .stream()
                    .filter(a -> {
                        String text = a.text().toLowerCase();
                        String href = a.attr("href").toLowerCase();
                        String title = a.attr("title").toLowerCase();
                        String ariaLabel = a.attr("aria-label").toLowerCase();
                        String candidate = text + " " +  href + " " + title + " " + ariaLabel;

                        return SIGNUP_KEYWARDS.stream().anyMatch(candidate::contains);
                    }).map(a -> a.absUrl("href"))
                    .filter(link -> !link.isBlank())
                    .findFirst()
                    .orElse(url);
        } catch (Exception e) {
            log.warn("회원가입 페이지 탐색 실패: {}", e.getMessage());
            return url;
        }
    }

    // 봇 감지 우회를 위함.
    private Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
                .header("Accept-Encoding", "gzip, deflate, br")
                .referrer("https://www.google.com")
                .timeout(10_000)
                .get();
    };
}