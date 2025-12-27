package com.example.demo.repository;

import com.example.demo.entity.SecurityKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 보안키 엔티티 데이터 접근 인터페이스
 * Spring Data JPA를 활용한 CRUD 및 커스텀 쿼리 메서드 제공
 * 메서드 이름 기반 쿼리 자동 생성으로 복잡한 SQL 작성 불필요
 */
@Repository
public interface SecurityKeyRepository extends JpaRepository<SecurityKey, Long> {
	java.util.List<SecurityKey> findByStatus(String status);
	java.util.List<SecurityKey> findByServiceCd(String serviceCd);

	Page<SecurityKey> findByStatus(String status, Pageable pageable);
	Page<SecurityKey> findByServiceCd(String serviceCd, Pageable pageable);
}
