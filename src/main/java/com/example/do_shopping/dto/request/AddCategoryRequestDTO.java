package com.example.do_shopping.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCategoryRequestDTO {
    @NotBlank(message = "Category name tidak boleh kosong")
    private String name;
}
