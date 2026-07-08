package com.example.do_shopping.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.do_shopping.entity.Customer;
import com.example.do_shopping.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public Page<Customer> getAllCustomers(int page, int size, String sortBy, String sortDirection) {
        String[] properties = sortBy.split(",");
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(properties).ascending()
                : Sort.by(properties).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return customerRepository.findAllActiveCustomers(pageable);
    }
}
