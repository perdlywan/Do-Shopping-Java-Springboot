package com.example.do_shopping.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAdminRequestDTO {
    @Email
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Integer is_active = 1;
}
