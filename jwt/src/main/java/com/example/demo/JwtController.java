package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * JWT 토큰 생성 및 검증을 위한 컨트롤러
 */
@Controller
@RequestMapping("/jwt")
public class JwtController {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * JWT 테스트 페이지 표시
     */
    @GetMapping("/testPage")
    public String showTestPage() {
        return "testPage";
    }

    /**
     * JWT 토큰 생성 엔드포인트
     * @param subject 토큰의 주체 (예: 사용자 ID)
     * @return 생성된 JWT 토큰
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateToken(@RequestParam String subject) {
        String token = jwtUtil.generateToken(subject);
        return ResponseEntity.ok("Generated JWT Token: " + token);
    }

    /**
     * JWT 토큰 만료 검증 엔드포인트
     * @param token 검증할 JWT 토큰
     * @return 토큰 만료 여부
     */
    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        boolean isExpired = jwtUtil.isTokenExpired(token);
        if (isExpired) {
            return ResponseEntity.ok("Token is expired");
        } else {
            return ResponseEntity.ok("Token is valid");
        }
    }

    /**
     * JWT 토큰에서 주체 추출 엔드포인트
     * @param token JWT 토큰
     * @return 토큰의 주체
     */
    @PostMapping("/subject")
    public ResponseEntity<String> getSubject(@RequestParam String token) {
        try {
            String subject = jwtUtil.getSubjectFromToken(token);
            return ResponseEntity.ok("Subject: " + subject);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

    /**
     * JWT 토큰 생성 폼 핸들러
     */
    @PostMapping("/generateForm")
    public String generateTokenForm(@RequestParam String subject, Model model) {
        String token = jwtUtil.generateToken(subject);
        model.addAttribute("generateResult", "Generated JWT Token: " + token);
        return "testPage";
    }

    /**
     * JWT 토큰 검증 폼 핸들러
     */
    @PostMapping("/validateForm")
    public String validateTokenForm(@RequestParam String token, Model model) {
        boolean isExpired = jwtUtil.isTokenExpired(token);
        String result = isExpired ? "Token is expired" : "Token is valid";
        model.addAttribute("validateResult", result);
        return "testPage";
    }

    /**
     * JWT 토큰에서 주체 추출 폼 핸들러
     */
    @PostMapping("/subjectForm")
    public String getSubjectForm(@RequestParam String token, Model model) {
        try {
            String subject = jwtUtil.getSubjectFromToken(token);
            model.addAttribute("subjectResult", "Subject: " + subject);
        } catch (Exception e) {
            model.addAttribute("subjectResult", "Invalid token");
        }
        return "testPage";
    }
}
