package com.example.do_shopping.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCategoryRequestDTO {
    @NotBlank(message = "name cannot be empty")
    private String name;
}
