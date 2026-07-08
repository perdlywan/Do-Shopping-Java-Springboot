package com.example.do_shopping.service;

import com.example.do_shopping.dto.request.AddAdminRequestDTO;
import com.example.do_shopping.entity.User;
import com.example.do_shopping.enums.Role;
import com.example.do_shopping.exception.custom.BusinessException;
import com.example.do_shopping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public void addAdmin(AddAdminRequestDTO request) {
        Optional<User> checkUser = userRepository.findByUsernameAndDeletedAtIsNull(request.getUsername());

        if (checkUser.isPresent()) {
            throw new BusinessException("Username already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);

        Integer is_active = request.getIsActive() == null ? 1 : request.getIsActive();
        user.setIsActive(is_active);
        userRepository.save(user);
    }

}
