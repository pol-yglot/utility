package com.example.demo.repository;

import com.example.demo.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 제휴기관 엔티티 데이터 접근 인터페이스
 * Spring Data JPA를 활용한 CRUD 및 커스텀 쿼리 메서드 제공
 */
@Repository
public interface PartnerRepository extends JpaRepository<Partner, String> {

    /**
     * 사업자번호로 제휴기관 조회
     * 사업자번호 존재 여부 확인용
     */
    Optional<Partner> findByBusinessNo(String businessNo);
}
