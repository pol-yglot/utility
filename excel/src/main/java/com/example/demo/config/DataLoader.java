package com.example.demo.config;

import com.example.demo.entity.SecurityKey;
import com.example.demo.repository.SecurityKeyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 초기 데이터 로딩 컴포넌트
 * 보안키 관리 시스템의 샘플 데이터를 자동 생성하여 개발/테스트 환경 구성
 * CommandLineRunner를 구현해 Spring Boot 시작 후 실행 보장
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final SecurityKeyRepository repository;

    /**
     * 생성자 주입으로 리포지토리 의존성 설정
     * Spring의 자동 구성으로 의존성 주입
     */
    public DataLoader(SecurityKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        /**
         * 발급 상태 키의 유일성 보장 로직
         * Excel 내보내기 시 중복 레코드 방지를 위해 하나의 발급 키만 유지
         * 기존 데이터가 있으면 가장 오래된 것만 보존하고 나머지는 폐기로 변경
         */
        java.util.List<SecurityKey> issuedList = repository.findByStatus("발급");

        if (issuedList == null || issuedList.isEmpty()) {
            // 발급 상태 샘플 데이터가 없으면 생성
            SecurityKey issued = new SecurityKey();
            issued.setServiceCd("SVC001");
            issued.setPrtnrId("PRT001");
            issued.setAcntId("AC001");
            issued.setCi("CI_ISSUED");
            issued.setCs("CS_ISSUED");
            issued.setMs("MS_ISSUED");
            issued.setStatus("발급");
            repository.save(issued);
            System.out.println("[DataLoader] Created missing '발급' sample record (serviceCd=SVC001).");
        } else if (issuedList.size() > 1) {
            // ID 기준 정렬로 가장 오래된 레코드 선택 (null-safe)
            issuedList.sort((a, b) -> {
                if (a.getId() == null && b.getId() == null) return 0;
                if (a.getId() == null) return 1;
                if (b.getId() == null) return -1;
                return a.getId().compareTo(b.getId());
            });
            SecurityKey keep = issuedList.get(0);
            // 나머지 레코드는 폐기 상태로 변경
            for (int i = 1; i < issuedList.size(); i++) {
                SecurityKey extra = issuedList.get(i);
                extra.setStatus("폐기");
                repository.save(extra);
            }
            System.out.println("[DataLoader] Found multiple '발급' records; kept id=" + keep.getId() + " and demoted others to '폐기'.");
        }

        // Ensure there is at least one other sample record for UI variety (갱신 and 폐기)
        boolean hasRenewed = repository.findByStatus("갱신").stream().findAny().isPresent();
        if (!hasRenewed) {
            SecurityKey renewed = new SecurityKey();
            renewed.setServiceCd("SVC002");
            renewed.setPrtnrId("PRT002");
            renewed.setAcntId("AC002");
            renewed.setCi("CI_RENEWED");
            renewed.setCs("CS_RENEWED");
            renewed.setMs("MS_RENEWED");
            renewed.setStatus("갱신");
            repository.save(renewed);
        }

        boolean hasRevoked = repository.findByStatus("폐기").stream().findAny().isPresent();
        if (!hasRevoked) {
            SecurityKey revoked = new SecurityKey();
            revoked.setServiceCd("SVC003");
            revoked.setPrtnrId("PRT003");
            revoked.setAcntId("AC003");
            revoked.setCi("CI_REVOKED");
            revoked.setCs("CS_REVOKED");
            revoked.setMs("MS_REVOKED");
            revoked.setStatus("폐기");
            repository.save(revoked);
        }

        System.out.println("[DataLoader] Current SecurityKey records:");
        repository.findAll().forEach(k -> System.out.println(" - id=" + k.getId() + ", status=" + k.getStatus() + ", serviceCd=" + k.getServiceCd()));
    }
}
