package com.example.do_shopping.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
