package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 보안키 관리 시스템 메인 애플리케이션 클래스
 * Spring Boot 자동 구성으로 웹 서버, 데이터베이스, 보안 설정 자동화
 * @SpringBootApplication으로 컴포넌트 스캔 및 자동 구성 활성화
 */
@SpringBootApplication
public class DemoApplication {

	/**
	 * 애플리케이션 시작점
	 * Spring Boot의 표준 main 메서드로 애플리케이션 컨텍스트 초기화
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
