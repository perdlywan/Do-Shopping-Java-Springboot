package com.example.do_shopping.dto.request.shippingAddress;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddShippingAddressRequestDTO {
    @NotBlank (message = "address cannot be empty")
    private String address;

    @NotBlank (message = "country cannot be empty")
    private String country;

    @NotBlank (message = "state cannot be empty")
    private String state;

    @NotBlank (message = "city cannot be empty")
    private String city;

    private String postalCode;
    private Boolean isDefault;
}
