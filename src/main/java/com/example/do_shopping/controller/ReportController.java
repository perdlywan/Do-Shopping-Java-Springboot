package com.example.do_shopping.controller;

import com.example.do_shopping.dto.response.DataResponseDTO;
import com.example.do_shopping.dto.response.report.SalesReportResponseDTO;
import com.example.do_shopping.dto.response.report.TopCustomerReportResponseDTO;
import com.example.do_shopping.dto.response.report.TopProductReportResponseDTO;
import com.example.do_shopping.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<DataResponseDTO> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<Object[]> data = reportService.getAllOrders(startDate, endDate);

        List<SalesReportResponseDTO> reports = data.stream().map(row -> {
            LocalDate tanggal = (row[0] instanceof java.sql.Date)
                    ? ((java.sql.Date) row[0]).toLocalDate()
                    : LocalDate.parse(row[0].toString());

            Integer totalOrder = ((Number) row[1]).intValue();
            BigDecimal totalPenjualan = (row[2] != null) ? (BigDecimal) row[2] : BigDecimal.ZERO;

            return new SalesReportResponseDTO(tanggal, totalOrder, totalPenjualan);
        }).collect(Collectors.toList());

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
        List<Object[]> data = reportService.getTopProducts(startDate, endDate);

        List<TopProductReportResponseDTO> reports = data.stream().map(row -> {
            String nama =  ((String) row[0]);
            Integer totalTerjual = ((Number) row[1]).intValue();

            return new TopProductReportResponseDTO(nama, totalTerjual);
        }).collect(Collectors.toList());

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
        List<Object[]> data = reportService.getTopCustomers(startDate, endDate);

        List<TopCustomerReportResponseDTO> reports = data.stream().map(row -> {
            Long id = (row[0] != null) ? ((Number) row[0]).longValue() : null;
            String nama = (row[1] != null) ? (String) row[1] : "Unknown";
            Integer totalOrder = (row[2] != null) ? ((Number) row[2]).intValue() : 0;

            return new TopCustomerReportResponseDTO(id, nama, totalOrder);
        }).collect(Collectors.toList());

        DataResponseDTO response = new DataResponseDTO(
                HttpStatus.OK.value(),
                reports
        );

        return ResponseEntity.ok(response);
    }
}
