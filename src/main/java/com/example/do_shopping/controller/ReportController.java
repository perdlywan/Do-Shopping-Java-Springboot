package com.example.do_shopping.controller;

import com.example.do_shopping.dto.response.DataResponseDTO;
import com.example.do_shopping.dto.response.report.SalesReportResponseDTO;
import com.example.do_shopping.dto.response.report.TopCustomerReportResponseDTO;
import com.example.do_shopping.dto.response.report.TopProductReportResponseDTO;
import com.example.do_shopping.service.ExcelExportService;
import com.example.do_shopping.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final ExcelExportService excelExportService;

    // ============ EXISTING JSON ENDPOINTS ============

    @GetMapping("/sales")
    public ResponseEntity<DataResponseDTO> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<SalesReportResponseDTO> reports = mapSalesData(startDate, endDate);

        DataResponseDTO response = new DataResponseDTO(
                HttpStatus.OK.value(),
                reports
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-products")
    public ResponseEntity<DataResponseDTO> getTopProductsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<TopProductReportResponseDTO> reports = mapTopProductsData(startDate, endDate);

        DataResponseDTO response = new DataResponseDTO(
                HttpStatus.OK.value(),
                reports
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-customers")
    public ResponseEntity<DataResponseDTO> getTopCustomersReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<TopCustomerReportResponseDTO> reports = mapTopCustomersData(startDate, endDate);

        DataResponseDTO response = new DataResponseDTO(
                HttpStatus.OK.value(),
                reports
        );

        return ResponseEntity.ok(response);
    }

    // ============ EXCEL EXPORT ENDPOINTS ============

    @GetMapping("/sales/export")
    public ResponseEntity<Resource> exportSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) throws IOException {
        List<SalesReportResponseDTO> reports = mapSalesData(startDate, endDate);
        ByteArrayInputStream stream = excelExportService.generateSalesReport(reports);

        return buildExcelResponse(stream, "laporan_penjualan.xlsx");
    }

    @GetMapping("/top-products/export")
    public ResponseEntity<Resource> exportTopProductsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) throws IOException {
        List<TopProductReportResponseDTO> reports = mapTopProductsData(startDate, endDate);
        ByteArrayInputStream stream = excelExportService.generateTopProductsReport(reports);

        return buildExcelResponse(stream, "top_produk.xlsx");
    }

    @GetMapping("/top-customers/export")
    public ResponseEntity<Resource> exportTopCustomersReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) throws IOException {
        List<TopCustomerReportResponseDTO> reports = mapTopCustomersData(startDate, endDate);
        ByteArrayInputStream stream = excelExportService.generateTopCustomersReport(reports);

        return buildExcelResponse(stream, "top_customer.xlsx");
    }

    // ============ PRIVATE HELPER METHODS ============

    private List<SalesReportResponseDTO> mapSalesData(LocalDate startDate, LocalDate endDate) {
        List<Object[]> data = reportService.getAllOrders(startDate, endDate);

        return data.stream().map(row -> {
            LocalDate tanggal = (row[0] instanceof java.sql.Date)
                    ? ((java.sql.Date) row[0]).toLocalDate()
                    : LocalDate.parse(row[0].toString());

            Integer totalOrder = ((Number) row[1]).intValue();
            BigDecimal totalPenjualan = (row[2] != null) ? (BigDecimal) row[2] : BigDecimal.ZERO;

            return new SalesReportResponseDTO(tanggal, totalOrder, totalPenjualan);
        }).collect(Collectors.toList());
    }

    private List<TopProductReportResponseDTO> mapTopProductsData(LocalDate startDate, LocalDate endDate) {
        List<Object[]> data = reportService.getTopProducts(startDate, endDate);

        return data.stream().map(row -> {
            String nama = ((String) row[0]);
            Integer totalTerjual = ((Number) row[1]).intValue();

            return new TopProductReportResponseDTO(nama, totalTerjual);
        }).collect(Collectors.toList());
    }

    private List<TopCustomerReportResponseDTO> mapTopCustomersData(LocalDate startDate, LocalDate endDate) {
        List<Object[]> data = reportService.getTopCustomers(startDate, endDate);

        return data.stream().map(row -> {
            String id = (row[0] != null) ? (String) row[0] : null;
            String nama = (row[1] != null) ? (String) row[1] : "Unknown";
            Integer totalOrder = (row[2] != null) ? ((Number) row[2]).intValue() : 0;

            return new TopCustomerReportResponseDTO(id, nama, totalOrder);
        }).collect(Collectors.toList());
    }

    private ResponseEntity<Resource> buildExcelResponse(ByteArrayInputStream stream, String filename) {
        InputStreamResource resource = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }
}
