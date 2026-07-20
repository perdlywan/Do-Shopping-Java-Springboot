package com.example.do_shopping.service;

import com.example.do_shopping.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getAllOrders(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return reportRepository.getFullSalesReport();
        } else {
            return reportRepository.getSalesReportByDate(startDate, endDate);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getTopProducts(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return reportRepository.getFullTopProductReport();
        } else {
            return reportRepository.getTopProductReportByDate(startDate, endDate);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getTopCustomers(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return reportRepository.getTopCustomerReport();
        } else {
            return reportRepository.getTopCustomerReportByDate(startDate, endDate);
        }
    }

}
