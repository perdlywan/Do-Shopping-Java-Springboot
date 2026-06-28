package com.example.do_shopping.service;

import com.example.do_shopping.config.SecurityUtil;
import com.example.do_shopping.dto.request.shippingAddress.AddShippingAddressRequestDTO;
import com.example.do_shopping.dto.request.shippingAddress.UpdateShippingAddressRequestDTO;
import com.example.do_shopping.entity.*;
import com.example.do_shopping.exception.custom.DataNotFoundException;
import com.example.do_shopping.repository.CustomerRepository;
import com.example.do_shopping.repository.ShippingAddressRepository;
import com.example.do_shopping.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShippingAddressService {
    private final AuthService authService;
    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    public Page<ShippingAddress> getMyShippingAddresses(int pageIndex, int size, String sortBy, String sortDirection){
        Customer customer = authService.getCurrentCustomer();
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(direction, sortBy));

        return shippingAddressRepository
                .findAllByCustomerIdAndDeletedAtIsNull(customer.getId(), pageable);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public ShippingAddress addAddress(AddShippingAddressRequestDTO request){
        Customer customer = authService.getCurrentCustomer();

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setCustomer(customer);
        shippingAddress.setAddress(request.getAddress());
        shippingAddress.setCountry(request.getCountry());
        shippingAddress.setState(request.getState());
        shippingAddress.setCity(request.getCity());
        shippingAddress.setPostalCode(request.getPostalCode());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            shippingAddressRepository
                    .findAllByCustomerIdAndDeletedAtIsNull(customer.getId())
                    .forEach(addr -> addr.setIsDefault(false));

            shippingAddress.setIsDefault(true);
        }

        return shippingAddressRepository.save(shippingAddress);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public ShippingAddress updateAddress(String id, UpdateShippingAddressRequestDTO request){
        ShippingAddress shippingAddress = shippingAddressRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("shipping_address_id " + id + " tidak ditemukan"));

        Customer customer = authService.getCurrentCustomer();

        if(!shippingAddress.getCustomer().getId().equals(customer.getId())){
            throw new AccessDeniedException("Anda tidak memiliki akses");
        }

        if(request.getAddress() != null && !request.getAddress().isBlank()){
            shippingAddress.setAddress(request.getAddress());
        }

        if(request.getCountry() != null && !request.getCountry().isBlank()){
            shippingAddress.setCountry(request.getCountry());
        }

        if(request.getState() != null && !request.getState().isBlank()){
            shippingAddress.setState(request.getState());
        }

        if(request.getCity() != null && !request.getCity().isBlank()){
            shippingAddress.setCity(request.getCity());
        }

        if(request.getPostalCode() != null && !request.getPostalCode().isBlank()){
            shippingAddress.setPostalCode(request.getPostalCode());
        }

        if (Boolean.TRUE.equals(request.getIsDefault())) {

            List<ShippingAddress> addresses =
                    shippingAddressRepository.findAllByCustomerIdAndDeletedAtIsNull(customer.getId());

            addresses.forEach(addr -> addr.setIsDefault(false));
            shippingAddress.setIsDefault(true);

            shippingAddressRepository.saveAll(addresses);
        }

        return shippingAddressRepository.save(shippingAddress);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public ShippingAddress deleteAddress(String id){
        ShippingAddress shippingAddress = shippingAddressRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("shipping_address_id " + id + " tidak ditemukan"));

        Customer customer = authService.getCurrentCustomer();

        if(!shippingAddress.getCustomer().getId().equals(customer.getId())){
            throw new AccessDeniedException("Anda tidak memiliki akses");
        }

        shippingAddress.setDeletedAt(LocalDateTime.now());
        shippingAddress.setDeletedBy(SecurityUtil.getCurrentUsername());
        return shippingAddressRepository.save(shippingAddress);
    }
}
