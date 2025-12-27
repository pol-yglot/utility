package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 MVC 설정 클래스
 * 정적 리소스 핸들링 및 웹 관련 구성 관리
 * favicon.ico 경로 매핑으로 브라우저 아이콘 표시 지원
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 정적 리소스 핸들러 설정
     * favicon.ico 요청을 static 폴더의 파일로 매핑
     * 브라우저 탭 아이콘 표시를 위한 설정
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.ico");
    }
}
