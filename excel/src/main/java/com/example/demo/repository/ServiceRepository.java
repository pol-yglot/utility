package com.example.demo.repository;

import com.example.demo.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 서비스 엔티티 데이터 접근 인터페이스
 * Spring Data JPA를 활용한 CRUD 및 커스텀 쿼리 메서드 제공
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

    /**
     * 상태별 서비스 조회
     * 활성/비활성 상태로 필터링하여 목록 반환
     */
    List<Service> findByStatus(String status);

    /**
     * 서비스명으로 검색
     * 서비스명에 포함된 키워드로 검색
     */
    List<Service> findByServiceNameContaining(String serviceName);
}
