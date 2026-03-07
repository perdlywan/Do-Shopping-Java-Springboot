package com.example.do_shopping.dto.response.shippingAddress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ShippingAddressResponseDTO {
    private Long id;
    private Long customerId;
    private String address;
    private String country;
    private String state;
    private String city;
    private String postalCode;
    private Boolean is_default;
}
