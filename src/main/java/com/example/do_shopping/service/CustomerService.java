package com.example.do_shopping.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.do_shopping.entity.Customer;
import com.example.do_shopping.entity.User;
import com.example.do_shopping.exception.custom.DataNotFoundException;
import com.example.do_shopping.repository.CustomerRepository;

import jakarta.transaction.Transactional;
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

    @PreAuthorize("hasRole('ADMIN')")
    public Customer getCustomerById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deactivateCustomer(String id, String adminId) {
        Customer customer = getCustomerById(id);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        customer.setDeletedAt(now);
        customer.setDeletedBy(adminId);
        customerRepository.save(customer);

        User user = customer.getUser();
        if (user != null) {
            user.setDeletedAt(now);
            user.setDeletedBy(adminId);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void activateCustomer(String id) {
        Customer customer = getCustomerById(id);
        customer.setDeletedAt(null);
        customer.setDeletedBy(null);
        customerRepository.save(customer);

        User user = customer.getUser();
        if (user != null) {
            user.setDeletedAt(null);
            user.setDeletedBy(null);
        }
    }
}
