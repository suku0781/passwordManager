package com.shk.personalProject.passwordManager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        //  jwt인증 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("basicAuth");

        return new OpenAPI().info(
                new Info().title("AI 비밀번호 매니저 API")
                        .description("웹사이트별 비밀번호 정책을 AI가 분석하고 AES-256-GCM으로 안전하게 저장하는 서비스")
                        .version("1.0")
                ).addSecurityItem(securityRequirement)
                 .components(new Components().addSecuritySchemes("bearerAuth", securityScheme));
    }
}
