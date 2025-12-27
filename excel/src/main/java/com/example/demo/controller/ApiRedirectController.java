package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * API 하위 호환성 유지 컨트롤러
 * 기존 클라이언트 코드 호환성을 위해 구 버전 API 경로를 신 버전으로 리다이렉트
 * API 버전 관리 전략의 일환으로 점진적 마이그레이션 지원
 */
@Controller
public class ApiRedirectController {

    /**
     * 구 버전 키 목록 API 리다이렉트
     * /api/keys 요청을 /api/v1/keys로 리다이렉트하여 하위 호환성 유지
     */
    @GetMapping("/api/keys")
    public void redirectKeys(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/v1/keys");
    }

    /**
     * 구 버전 키 검색 API 리다이렉트
     * /api/keys/search 요청을 /api/v1/keys/search로 리다이렉트
     */
    @GetMapping("/api/keys/search")
    public void redirectSearch(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/v1/keys/search");
    }
}
