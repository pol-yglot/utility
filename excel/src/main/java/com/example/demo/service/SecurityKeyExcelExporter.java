package com.example.demo.service;

import com.example.demo.entity.SecurityKey;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 보안키 Excel 내보내기 서비스
 * Apache POI를 활용한 표준 Excel 파일 생성
 * 금융 시스템의 감사 요구사항을 위한 구조화된 데이터 출력
 */
@Service
public class SecurityKeyExcelExporter {

    /**
     * 날짜 포맷터
     * Excel 호환성을 위한 표준 날짜 형식 사용
     */
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 보안키 목록을 Excel 파일로 내보내기
     * 표준 XLSX 포맷으로 헤더와 데이터 행 생성
     * null 값 처리 및 자동 컬럼 너비 조정
     * 사업자번호를 비밀번호로 암호화하여 보안 강화
     */
    public void export(List<SecurityKey> keys, OutputStream out, String password) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("SecurityKeys");

            // Header
            Row header = sheet.createRow(0);
            String[] headers = new String[]{"ID","ServiceCd","PrtnrId","AcntId","CI","CS","MS","Status","CreatedAt","UpdatedAt","DeletedAt"};
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            for (int i = 0; i < headers.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIdx = 1;
            for (SecurityKey k : keys) {
                Row r = sheet.createRow(rowIdx++);
                r.createCell(0).setCellValue(k.getId() == null ? "" : k.getId().toString());
                r.createCell(1).setCellValue(nullToEmpty(k.getServiceCd()));
                r.createCell(2).setCellValue(nullToEmpty(k.getPrtnrId()));
                r.createCell(3).setCellValue(nullToEmpty(k.getAcntId()));
                r.createCell(4).setCellValue(nullToEmpty(k.getCi()));
                r.createCell(5).setCellValue(nullToEmpty(k.getCs()));
                r.createCell(6).setCellValue(nullToEmpty(k.getMs()));
                r.createCell(7).setCellValue(nullToEmpty(k.getStatus()));
                r.createCell(8).setCellValue(k.getCreatedAt() == null ? "" : k.getCreatedAt().format(DTF));
                r.createCell(9).setCellValue(k.getUpdatedAt() == null ? "" : k.getUpdatedAt().format(DTF));
                r.createCell(10).setCellValue(k.getDeletedAt() == null ? "" : k.getDeletedAt().format(DTF));
            }

            // Autosize
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 암호화 적용
            if (password != null && !password.trim().isEmpty()) {
                encryptWorkbook(workbook, out, password);
            } else {
                workbook.write(out);
                out.flush();
            }
        }
    }

    /**
     * Excel 워크북을 암호화하여 출력 스트림에 작성
     * 사업자번호를 비밀번호로 사용하여 파일 보호
     *
     * @param workbook 암호화할 워크북
     * @param out 출력 스트림
     * @param password 암호화 비밀번호 (사업자번호)
     * @throws IOException 파일 처리 중 발생할 수 있는 예외
     */
    private void encryptWorkbook(XSSFWorkbook workbook, OutputStream out, String password) throws IOException {
        try (POIFSFileSystem fs = new POIFSFileSystem()) {
            EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
            Encryptor encryptor = info.getEncryptor();
            encryptor.confirmPassword(password);

            try (java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream()) {
                workbook.write(bos);
                try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bos.toByteArray());
                     java.io.OutputStream os = encryptor.getDataStream(fs)) {
                    bis.transferTo(os);
                }
            }

            fs.writeFilesystem(out);
            out.flush();
        } catch (GeneralSecurityException e) {
            throw new IOException("Excel 파일 암호화 중 오류 발생", e);
        }
    }

    /**
     * null 값 처리 헬퍼 메서드
     * Excel 셀에 null 값이 들어갈 경우 빈 문자열로 변환
     * Excel 호환성을 위한 안전한 값 변환
     */
    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
