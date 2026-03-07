package com.example.do_shopping.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SalesReportResponseDTO {
    private LocalDate tanggal;
    private Integer totalOrder;
    private BigDecimal totalPenjualan;
}
