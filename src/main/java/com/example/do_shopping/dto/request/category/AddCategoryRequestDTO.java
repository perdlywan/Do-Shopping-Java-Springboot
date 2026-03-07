package com.example.do_shopping.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCategoryRequestDTO {
    @NotBlank(message = "name tidak boleh kosong")
    private String name;
}
