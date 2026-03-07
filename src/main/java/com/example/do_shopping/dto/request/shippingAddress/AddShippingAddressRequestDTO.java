package com.example.do_shopping.dto.request.shippingAddress;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddShippingAddressRequestDTO {
    @NotBlank (message = "address tidak boleh kosong")
    private String address;

    @NotBlank (message = "country tidak boleh kosong")
    private String country;

    @NotBlank (message = "state tidak boleh kosong")
    private String state;

    @NotBlank (message = "city tidak boleh kosong")
    private String city;

    private String postalCode;
    private Boolean isDefault;
}
