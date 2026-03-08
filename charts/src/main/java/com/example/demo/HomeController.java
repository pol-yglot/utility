package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메인 홈 컨트롤러
 * 애플리케이션 루트 경로 처리 및 기본 페이지 라우팅
 * 보안키 관리 시스템의 시작점으로 사용자 진입점 제공
 */
@Controller
public class HomeController {

    /**
     * 루트 경로 핸들러
     * 애플리케이션 시작 시 기본 페이지로 리다이렉트
     * 실제 키 관리 기능은 별도 컨트롤러에서 처리
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

}
