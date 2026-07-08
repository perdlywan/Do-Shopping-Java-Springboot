package com.example.do_shopping.controller;

import com.example.do_shopping.dto.request.customer.CustomerRegisterRequestDTO;
import com.example.do_shopping.dto.request.auth.UserLoginRequestDTO;
import com.example.do_shopping.dto.response.ActionSuccessResponseDTO;
import com.example.do_shopping.dto.response.RegisterResponseDTO;
import com.example.do_shopping.entity.Customer;
import com.example.do_shopping.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDTO request){
        java.util.Map<String, String> authData = authService.login(request);

        return ResponseEntity.ok(Map.of(
                "token", authData.get("token"),
                "role", authData.get("role"),
                "type", "Bearer"
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<ActionSuccessResponseDTO> register(@Valid @RequestBody CustomerRegisterRequestDTO request){
        Customer customer = authService.register(request);

        RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO(
                customer.getUser().getId(),
                customer.getUser().getUsername()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Registration successful",
                registerResponseDTO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
