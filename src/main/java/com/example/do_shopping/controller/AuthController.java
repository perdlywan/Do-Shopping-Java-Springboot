package com.example.do_shopping.controller;

import com.example.do_shopping.dto.request.CustomerRegisterRequestDTO;
import com.example.do_shopping.dto.request.UserLoginRequestDTO;
import com.example.do_shopping.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        String token = authService.login(request);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "type", "Bearer"
        ));
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody CustomerRegisterRequestDTO request){
        authService.register(request);
    }
}
