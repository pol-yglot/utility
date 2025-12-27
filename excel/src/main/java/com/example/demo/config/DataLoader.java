package com.example.demo.config;

import com.example.demo.entity.SecurityKey;
import com.example.demo.repository.SecurityKeyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final SecurityKeyRepository repository;

    public DataLoader(SecurityKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ensure exactly one '발급' (issued) record exists.
        // If none exists, create one. If multiple exist, keep the oldest (by id) and demote others to '폐기'.
        java.util.List<SecurityKey> issuedList = repository.findByStatus("발급");

        if (issuedList == null || issuedList.isEmpty()) {
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
            // sort by id (null-safe) and keep the first
            issuedList.sort((a, b) -> {
                if (a.getId() == null && b.getId() == null) return 0;
                if (a.getId() == null) return 1;
                if (b.getId() == null) return -1;
                return a.getId().compareTo(b.getId());
            });
            SecurityKey keep = issuedList.get(0);
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
