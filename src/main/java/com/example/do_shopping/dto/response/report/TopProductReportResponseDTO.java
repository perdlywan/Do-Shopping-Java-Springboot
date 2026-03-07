package com.example.do_shopping.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TopProductReportResponseDTO {
    private String nama;
    private Integer totalTerjual;
}
