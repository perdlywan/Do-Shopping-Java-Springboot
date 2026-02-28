package com.example.do_shopping.controller;

import com.example.do_shopping.dto.request.AddAdminRequestDTO;
import com.example.do_shopping.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public void addAdmin(@RequestBody AddAdminRequestDTO request) {
        userService.addAdmin(request);
    }
}
