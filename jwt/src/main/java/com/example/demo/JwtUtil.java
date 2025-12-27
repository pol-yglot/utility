package com.example.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 유틸리티 클래스
 */
@Component
public class JwtUtil {

    // JWT 서명에 사용할 비밀 키 (실제 운영 환경에서는 환경 변수나 설정 파일에서 가져와야 함)
    private static final String SECRET_KEY = "mySecretKeyForJwtTokenGenerationAndValidation12345678901234567890";
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 토큰 만료 시간: 10분
    private static final long EXPIRATION_TIME = 10 * 60 * 1000; // 10분을 밀리초로 변환

    /**
     * JWT 토큰 생성
     * @param subject 토큰의 주체 (예: 사용자 ID)
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰에서 주체 추출
     * @param token JWT 토큰
     * @return 토큰의 주체
     */
    public String getSubjectFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * JWT 토큰 만료 여부 검증
     * @param token JWT 토큰
     * @return 만료되었으면 true, 유효하면 false
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // 토큰이 유효하지 않거나 파싱에 실패한 경우 만료된 것으로 간주
            return true;
        }
    }

    /**
     * JWT 토큰 유효성 검증 (만료되지 않았고, 서명이 유효한지 확인)
     * @param token JWT 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
