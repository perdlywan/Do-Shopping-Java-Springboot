package com.example.do_shopping.controller;

import com.example.do_shopping.dto.request.shippingAddress.AddShippingAddressRequestDTO;
import com.example.do_shopping.dto.request.shippingAddress.UpdateShippingAddressRequestDTO;
import com.example.do_shopping.dto.response.ActionSuccessResponseDTO;
import com.example.do_shopping.dto.response.PagedResponseDTO;
import com.example.do_shopping.dto.response.product.ProductResponseDTO;
import com.example.do_shopping.dto.response.shippingAddress.ShippingAddressResponseDTO;
import org.springframework.data.domain.Page;
import com.example.do_shopping.entity.Product;
import com.example.do_shopping.entity.ShippingAddress;
import com.example.do_shopping.service.ShippingAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shippingaddresses")
@RequiredArgsConstructor
public class ShippingAddressController {
    private final ShippingAddressService shippingAddressService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<ShippingAddressResponseDTO>> getMyAddress(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        int pageIndex = page > 0 ? page - 1 : 0;
        Page<ShippingAddress> shippingAddressPage =  shippingAddressService.getMyShippingAddresses(pageIndex, size, sortBy, sortDirection);

        List<ShippingAddressResponseDTO> shippingAddressResponseDTO = shippingAddressPage.getContent().stream()
                .map(shippingAddress -> new ShippingAddressResponseDTO (
                        shippingAddress.getId(),
                        shippingAddress.getCustomer().getId(),
                        shippingAddress.getAddress(),
                        shippingAddress.getCountry(),
                        shippingAddress.getState(),
                        shippingAddress.getCity(),
                        shippingAddress.getPostalCode(),
                        shippingAddress.getIsDefault()
                ))
                .collect(Collectors.toList());

        PagedResponseDTO<ShippingAddressResponseDTO> response = new PagedResponseDTO<>(
                HttpStatus.OK.value(),
                shippingAddressResponseDTO,
                shippingAddressPage.getNumber() + 1,
                shippingAddressPage.getSize(),
                shippingAddressPage.getTotalElements(),
                shippingAddressPage.getTotalPages(),
                shippingAddressPage.isLast()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<ActionSuccessResponseDTO> addAdress(@Valid @RequestBody AddShippingAddressRequestDTO request){
        ShippingAddress shippingAddress = shippingAddressService.addAddress(request);

        ShippingAddressResponseDTO shippingAddressResponseDTO = new ShippingAddressResponseDTO(
                shippingAddress.getId(),
                shippingAddress.getCustomer().getId(),
                shippingAddress.getAddress(),
                shippingAddress.getCountry(),
                shippingAddress.getState(),
                shippingAddress.getCity(),
                shippingAddress.getPostalCode(),
                shippingAddress.getIsDefault()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Alamat pengiriman berhasil ditambahkan",
                shippingAddressResponseDTO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionSuccessResponseDTO> updateAddress(@PathVariable("id") String id, @Valid @RequestBody UpdateShippingAddressRequestDTO request){
        ShippingAddress shippingAddress =  shippingAddressService.updateAddress(id, request);

        ShippingAddressResponseDTO shippingAddressResponseDTO = new ShippingAddressResponseDTO(
                shippingAddress.getId(),
                shippingAddress.getCustomer().getId(),
                shippingAddress.getAddress(),
                shippingAddress.getCountry(),
                shippingAddress.getState(),
                shippingAddress.getCity(),
                shippingAddress.getPostalCode(),
                shippingAddress.getIsDefault()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Alamat pengiriman berhasil diupdate",
                shippingAddressResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionSuccessResponseDTO> deleteAddress(@PathVariable("id") String id){
        ShippingAddress shippingAddress =  shippingAddressService.deleteAddress(id);

        ShippingAddressResponseDTO shippingAddressResponseDTO = new ShippingAddressResponseDTO(
                shippingAddress.getId(),
                shippingAddress.getCustomer().getId(),
                shippingAddress.getAddress(),
                shippingAddress.getCountry(),
                shippingAddress.getState(),
                shippingAddress.getCity(),
                shippingAddress.getPostalCode(),
                shippingAddress.getIsDefault()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Alamat pengiriman berhasil dihapus",
                shippingAddressResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
