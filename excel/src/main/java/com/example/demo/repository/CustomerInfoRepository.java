package com.example.demo.repository;

import com.example.demo.entity.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 고객정보 엔티티 데이터 접근 인터페이스
 * Spring Data JPA를 활용한 CRUD 및 커스텀 쿼리 메서드 제공
 */
@Repository
public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, String> {

    /**
     * 상태별 고객정보 조회
     * 정상/정지/해지 상태로 필터링하여 목록 반환
     */
    List<CustomerInfo> findByStatus(String status);

    /**
     * 고객명으로 검색
     * 고객명에 포함된 키워드로 검색
     */
    List<CustomerInfo> findByCustomerNameContaining(String customerName);

    /**
     * 고객 유형별 조회
     * 개인/법인 구분으로 필터링
     */
    List<CustomerInfo> findByCustomerType(String customerType);

    /**
     * 제휴기관 ID로 고객정보 조회
     * 특정 제휴기관에 속한 고객들 조회
     */
    List<CustomerInfo> findByPartner_PrtnrId(String prtnrId);
}
