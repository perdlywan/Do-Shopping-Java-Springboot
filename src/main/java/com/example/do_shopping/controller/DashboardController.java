package com.example.do_shopping.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.do_shopping.dto.response.DataResponseDTO;
import com.example.do_shopping.dto.response.dashboard.DashboardSummaryResponseDTO;
import com.example.do_shopping.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DataResponseDTO> getDashboardSummary() {
        DashboardSummaryResponseDTO summary = dashboardService.getDashboardSummary();
        
        DataResponseDTO response = new DataResponseDTO(
            HttpStatus.OK.value(),
            summary
        );
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
