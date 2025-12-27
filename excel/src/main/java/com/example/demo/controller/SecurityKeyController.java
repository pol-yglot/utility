package com.example.demo.controller;

import com.example.demo.entity.SecurityKey;
import com.example.demo.repository.SecurityKeyRepository;
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

@Controller
@RequestMapping("")
public class SecurityKeyController {

    private final SecurityKeyRepository repository;
    private final SecurityKeyExcelExporter excelExporter;

    public SecurityKeyController(SecurityKeyRepository repository, SecurityKeyExcelExporter excelExporter) {
        this.repository = repository;
        this.excelExporter = excelExporter;
    }

    // GET /corp/keyInfo - render JSP with keys (server-side) with paging
    @GetMapping("/corp/keyInfo")
    public String listAll(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) String serviceCd,
                          Model model) {

        org.springframework.data.domain.Page<SecurityKey> pageResult;

        if (status != null && !status.isEmpty()) {
            pageResult = repository.findByStatus(status, org.springframework.data.domain.PageRequest.of(page, size));
        } else if (serviceCd != null && !serviceCd.isEmpty()) {
            pageResult = repository.findByServiceCd(serviceCd, org.springframework.data.domain.PageRequest.of(page, size));
        } else {
            pageResult = repository.findAll(org.springframework.data.domain.PageRequest.of(page, size));
        }

        model.addAttribute("keys", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", pageResult.getSize());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("status", status == null ? "" : status);
        model.addAttribute("serviceCd", serviceCd == null ? "" : serviceCd);
        return "corp/keyInfo";
    }

    // GET /api/v1/keys/{id} - get by id (JSON)
    @GetMapping(value = "/api/v1/keys/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SecurityKey> getById(@PathVariable Long id) {
        Optional<SecurityKey> found = repository.findById(id);
        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/v1/keys/search?status=발급 (JSON)
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

    // GET /api/v1/keys/export - download Excel file (.xlsx) of matching keys
    @GetMapping(value = "/api/v1/keys/export")
    public void exportExcel(HttpServletResponse response) throws IOException {

        // Export only the single '발급' record. If multiple exist, DataLoader ensures extras were demoted.
        java.util.List<SecurityKey> issued = repository.findByStatus("발급");
        SecurityKey target = null;
        if (issued != null && !issued.isEmpty()) {
            // pick the first (should be unique after DataLoader run)
            target = issued.get(0);
        }

        java.util.List<SecurityKey> toExport = target == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(target);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = "security-keys-issued.xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        excelExporter.export(toExport, response.getOutputStream());
    }
}
