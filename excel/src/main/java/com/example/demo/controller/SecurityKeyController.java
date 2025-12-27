package com.example.demo.controller;

import com.example.demo.entity.SecurityKey;
import com.example.demo.entity.Partner;
import com.example.demo.repository.SecurityKeyRepository;
import com.example.demo.repository.PartnerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import com.example.demo.service.SecurityKeyExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * 보안키 관리 메인 컨트롤러
 * 웹 UI와 REST API를 통합 제공
 * 페이징, 필터링, Excel 내보내기 기능 포함
 */
@Controller
@RequestMapping("")
public class SecurityKeyController {

    private final SecurityKeyRepository repository;
    private final SecurityKeyExcelExporter excelExporter;
    private final PartnerRepository partnerRepository;

    /**
     * 생성자 주입으로 의존성 설정
     * 리포지토리와 Excel 내보내기 서비스 주입
     */
    public SecurityKeyController(SecurityKeyRepository repository, SecurityKeyExcelExporter excelExporter, PartnerRepository partnerRepository) {
        this.repository = repository;
        this.excelExporter = excelExporter;
        this.partnerRepository = partnerRepository;
    }

    /**
     * 보안키 목록 조회 및 JSP 렌더링
     * 페이징과 필터링을 지원하는 웹 페이지 표시
     * 상태 또는 서비스 코드 기반 필터링으로 대량 데이터 효율적 조회
     */
    @GetMapping("/corp/keyInfo")
    public String listAll(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) String serviceCd,
                          Model model) {

        org.springframework.data.domain.Page<SecurityKey> pageResult;

        // 필터링 조건에 따른 페이징 쿼리 실행
        if (status != null && !status.isEmpty()) {
            pageResult = repository.findByStatus(status, org.springframework.data.domain.PageRequest.of(page, size));
        } else if (serviceCd != null && !serviceCd.isEmpty()) {
            pageResult = repository.findByServiceCd(serviceCd, org.springframework.data.domain.PageRequest.of(page, size));
        } else {
            pageResult = repository.findAll(org.springframework.data.domain.PageRequest.of(page, size));
        }

        // JSP 템플릿에 페이징 정보와 필터 상태 전달
        model.addAttribute("keys", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", pageResult.getSize());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("status", status == null ? "" : status);
        model.addAttribute("serviceCd", serviceCd == null ? "" : serviceCd);
        return "corp/keyInfo";
    }

    /**
     * 보안키 단건 조회 API
     * ID 기반 조회로 특정 키의 상세 정보 확인
     * 존재하지 않는 ID는 404 반환으로 명확한 에러 처리
     *
     * @param id 조회할 보안키의 고유 ID
     * @return SecurityKey 객체 또는 404 Not Found
     * @apiNote GET /api/v1/keys/{id}
     * @example curl -X GET "http://localhost:8080/api/v1/keys/1"
     */
    @GetMapping(value = "/api/v1/keys/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SecurityKey> getById(@PathVariable Long id) {
        Optional<SecurityKey> found = repository.findById(id);
        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 보안키 검색 API
     * 상태 또는 서비스 코드 기반 필터링
     * 다중 조건 지원을 위해 별도 엔드포인트로 분리
     */
    @GetMapping(value = "/api/v1/keys/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SecurityKey>> search(@RequestParam(required = false) String status,
                                                    @RequestParam(required = false) String serviceCd) {
        if (status != null) {
            return ResponseEntity.ok(repository.findByStatus(status));
        }
        if (serviceCd != null) {
            return ResponseEntity.ok(repository.findByServiceCd(serviceCd));
        }
        return ResponseEntity.ok(repository.findAll());
    }

    /**
     * 사업자번호 검증 API
     * 사업자번호 존재 여부 확인
     * Excel 다운로드 권한 검증용
     *
     * @param request 요청 본문에 포함된 사업자번호 (JSON: {"businessNo": "1234567890"})
     * @return 검증 결과 (valid: true/false, message: 검증 메시지)
     * @apiNote POST /api/v1/validate-business-no
     * @example curl -X POST "http://localhost:8080/api/v1/validate-business-no" \
     *         -H "Content-Type: application/json" \
     *         -d '{"businessNo": "1234567890"}'
     */
    @PostMapping(value = "/api/v1/validate-business-no", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validateBusinessNo(@RequestBody Map<String, String> request) {
        String businessNo = request.get("businessNo");
        Map<String, Object> response = new HashMap<>();

        if (businessNo == null || businessNo.trim().isEmpty()) {
            response.put("valid", false);
            response.put("message", "사업자번호를 입력해주세요");
            return ResponseEntity.badRequest().body(response);
        }

        // 10자리 숫자 검증
        if (!businessNo.matches("\\d{10}")) {
            response.put("valid", false);
            response.put("message", "사업자번호를 확인해주세요");
            return ResponseEntity.badRequest().body(response);
        }

        // DB에서 존재 여부 확인
        Optional<Partner> partner = partnerRepository.findByBusinessNo(businessNo);
        if (partner.isPresent()) {
            response.put("valid", true);
            response.put("message", "검증 성공");
            return ResponseEntity.ok(response);
        } else {
            response.put("valid", false);
            response.put("message", "사업자번호를 확인해주세요");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 보안키 Excel 다운로드 API
     * 발급 상태의 키만 추출해 엑셀 파일로 제공
     * DataLoader에서 중복 발급 키를 정리하므로 첫 번째 레코드만 선택
     * 금융 시스템의 감사 요구사항을 위해 파일명에 타임스탬프 대신 고정명 사용
     * 사업자번호를 비밀번호로 Excel 파일 암호화하여 보안 강화
     *
     * @param response HTTP 응답 객체 (파일 다운로드용)
     * @param businessNo 사업자번호 (비밀번호로 사용)
     * @throws IOException 파일 생성 중 발생할 수 있는 예외
     * @apiNote GET /api/v1/keys/export?businessNo=1234567890
     * @example curl -X GET "http://localhost:8080/api/v1/keys/export?businessNo=1234567890" -o security-keys-issued.xlsx
     */
    @GetMapping(value = "/api/v1/keys/export")
    public void exportExcel(HttpServletResponse response, @RequestParam String businessNo, @RequestParam(required = false) String status) throws IOException {

        // 사업자번호 검증
        if (businessNo == null || businessNo.trim().isEmpty() || !businessNo.matches("\\d{10}")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효한 사업자번호를 입력해주세요");
            return;
        }

        // DB에서 사업자번호 존재 여부 확인
        Optional<Partner> partner = partnerRepository.findByBusinessNo(businessNo);
        if (!partner.isPresent()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "등록되지 않은 사업자번호입니다");
            return;
        }

        // 발급 상태 키만 추출 - DataLoader에서 중복 정리 보장
        java.util.List<SecurityKey> issued = repository.findByStatus("발급");
        SecurityKey target = null;
        if (issued != null && !issued.isEmpty()) {
            // DataLoader 실행 후 유일한 레코드 선택
            target = issued.get(0);
        }

        java.util.List<SecurityKey> toExport = target == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(target);

        // Excel 파일 다운로드 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = "security-keys-issued.xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        // 사업자번호를 비밀번호로 사용하여 Excel 파일 암호화
        excelExporter.export(toExport, response.getOutputStream(), businessNo);
    }
}
