package com.example.demo.repository;

import com.example.demo.entity.SecurityKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityKeyRepository extends JpaRepository<SecurityKey, Long> {
	java.util.List<SecurityKey> findByStatus(String status);
	java.util.List<SecurityKey> findByServiceCd(String serviceCd);

	Page<SecurityKey> findByStatus(String status, Pageable pageable);
	Page<SecurityKey> findByServiceCd(String serviceCd, Pageable pageable);
}
