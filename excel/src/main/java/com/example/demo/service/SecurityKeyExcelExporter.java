package com.example.demo.service;

import com.example.demo.entity.SecurityKey;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SecurityKeyExcelExporter {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void export(List<SecurityKey> keys, OutputStream out) throws IOException {
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

            workbook.write(out);
            out.flush();
        }
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
