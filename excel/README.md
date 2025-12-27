# 보안키 관리 시스템

금융 서비스에서 사용되는 암호화 키의 발급, 갱신, 폐기 상태를 관리하는 웹 애플리케이션입니다. Spring Boot 기반으로 구축되어 안정성과 확장성을 보장합니다.

## 기술 스택

- **Backend**: Spring Boot 3.x, Spring Data JPA
- **Frontend**: JSP, jQuery (간단한 동적 UI)
- **Database**: H2 (개발용), PostgreSQL/MySQL (운영용)
- **Build Tool**: Maven
- **Excel Export**: Apache POI
- **Java Version**: 17+

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/          # 설정 클래스 (WebConfig 등)
│   │   ├── controller/      # 웹 컨트롤러 (REST API + JSP)
│   │   ├── entity/          # JPA 엔티티 (SecurityKey)
│   │   ├── repository/      # 데이터 접근 계층
│   │   └── service/         # 비즈니스 로직 (Excel 내보내기)
│   └── resources/
│       ├── static/          # 정적 리소스 (CSS, JS, 이미지)
│       └── templates/       # JSP 템플릿
└── test/                    # 단위/통합 테스트
```

## 실행 방법

### 로컬 개발 환경

1. **Java 17 설치 확인**
   ```bash
   java -version
   ```

2. **프로젝트 클론 및 의존성 설치**
   ```bash
   git clone <repository-url>
   cd security-key-management
   mvn clean install
   ```

3. **애플리케이션 실행**
   ```bash
   mvn spring-boot:run
   # 또는
   ./mvnw spring-boot:run
   ```

4. **접근 URL**
   - 웹 UI: http://localhost:8080/corp/keyInfo
   - H2 Console: http://localhost:8080/h2-console

### 데이터베이스 설정

운영 환경에서는 `application.properties`에 다음 설정 추가:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/security_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
```

## 주요 기능 흐름

### 1. 키 조회 및 필터링
- `/corp/keyInfo` 페이지를 통해 보안키 목록 조회
- 상태(발급/갱신/폐기) 또는 서비스 코드로 필터링
- 페이징 처리로 대량 데이터 효율적 조회

### 2. API 연동
- REST API로 외부 시스템 연동 지원
- `/api/v1/keys/{id}`: 단건 조회
- `/api/v1/keys/search`: 조건 검색
- `/api/v1/keys/export`: Excel 다운로드

### 3. Excel 내보내기
- 발급 상태 키만 선택적 내보내기
- Apache POI 기반 표준 Excel 포맷
- 감사 추적을 위한 고정 파일명 사용

## 개발 시 주의사항

### 1. 타임존 처리
- LocalDateTime 사용으로 서버 타임존 독립성 보장
- JSP 표시용 Date 변환 메서드 제공 (`getCreatedAtAsDate()`)

### 2. 데이터 일관성
- `@PrePersist`, `@PreUpdate`로 타임스탬프 자동 관리
- DataLoader에서 중복 발급 키 정리 로직 구현

### 3. 보안 고려사항
- 프로퍼티 파일 gitignore 처리로 민감 정보 보호
- API 엔드포인트에 대한 접근 제어 검토 필요

### 4. 성능 최적화
- 페이징 쿼리로 대량 데이터 처리
- Excel 내보내기 시 메모리 사용량 모니터링

## 자주 발생하는 이슈 / 트러블슈팅

### 1. LocalDateTime 변환 에러
```
Cannot convert LocalDateTime to Date
```
**해결**: 엔티티에 `getCreatedAtAsDate()` 메서드 추가로 JSP 호환성 확보

### 2. favicon.ico 404 에러
```
Failed to convert 'favicon.ico' to Long
```
**해결**: WebConfig에 정적 리소스 핸들러 추가 또는 API 경로 충돌 방지

### 3. Excel 다운로드 시 빈 파일
**원인**: 발급 상태 키가 없을 경우
**해결**: DataLoader 실행 확인 또는 조건 로직 검토

### 4. 페이징 파라미터 전달 실패
**해결**: JavaScript로 동적 링크 업데이트 구현

### 5. 프로퍼티 파일 커밋 방지
**해결**: `.gitignore`에 `*.properties` 추가
