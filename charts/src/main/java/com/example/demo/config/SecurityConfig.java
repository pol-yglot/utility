package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스
 * 웹 페이지에 대한 CSRF 보호 유지, API 엔드포인트에 대한 CSRF 비활성화
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")  // API 엔드포인트에 대한 CSRF 비활성화
            )
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // 모든 요청 허용 (개발 환경용)
            );

        return http.build();
    }
}
