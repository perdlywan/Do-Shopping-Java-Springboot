package com.example.do_shopping.dto.request.shippingAddress;

import lombok.Data;

@Data
public class UpdateShippingAddressRequestDTO {
    private String address;
    private String country;
    private String state;
    private String city;
    private String postalCode;
    private Boolean isDefault;
}
