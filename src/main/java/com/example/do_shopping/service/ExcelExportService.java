package com.example.do_shopping.service;
import com.example.do_shopping.dto.response.report.SalesReportResponseDTO;
import com.example.do_shopping.dto.response.report.TopCustomerReportResponseDTO;
import com.example.do_shopping.dto.response.report.TopProductReportResponseDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
@Service
public class ExcelExportService {
    // ============ SALES REPORT ============
    public ByteArrayInputStream generateSalesReport(List<SalesReportResponseDTO> data) throws IOException {
        String[] headers = {"Tanggal", "Total Order", "Total Penjualan"};
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Laporan Penjualan");
            // Header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            // Data rows
            int rowIdx = 1;
            for (SalesReportResponseDTO item : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getTanggal().toString());
                row.createCell(1).setCellValue(item.getTotalOrder());
                row.createCell(2).setCellValue(
                    item.getTotalPenjualan() != null
                        ? item.getTotalPenjualan().doubleValue()
                        : 0
                );
            }
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            return toByteArrayInputStream(workbook);
        }
    }
    // ============ TOP PRODUCTS REPORT ============
    public ByteArrayInputStream generateTopProductsReport(List<TopProductReportResponseDTO> data) throws IOException {
        String[] headers = {"Nama Produk", "Total Terjual"};
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Top Produk");
            CellStyle headerStyle = createHeaderStyle(workbook);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowIdx = 1;
            for (TopProductReportResponseDTO item : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getNama());
                row.createCell(1).setCellValue(item.getTotalTerjual());
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            return toByteArrayInputStream(workbook);
        }
    }
    // ============ TOP CUSTOMERS REPORT ============
    public ByteArrayInputStream generateTopCustomersReport(List<TopCustomerReportResponseDTO> data) throws IOException {
        String[] headers = {"ID", "Nama Customer", "Total Order"};
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Top Customer");
            CellStyle headerStyle = createHeaderStyle(workbook);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowIdx = 1;
            for (TopCustomerReportResponseDTO item : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getId() != null ? item.getId() : "");
                row.createCell(1).setCellValue(item.getNama());
                row.createCell(2).setCellValue(item.getTotalOrder());
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            return toByteArrayInputStream(workbook);
        }
    }
    // ============ HELPER METHODS ============
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
    private ByteArrayInputStream toByteArrayInputStream(Workbook workbook) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}