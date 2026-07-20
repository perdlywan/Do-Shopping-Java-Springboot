package com.example.do_shopping.service;

import com.example.do_shopping.dto.request.customer.CustomerRegisterRequestDTO;
import com.example.do_shopping.dto.request.auth.UserLoginRequestDTO;
import com.example.do_shopping.entity.Customer;
import com.example.do_shopping.entity.User;
import com.example.do_shopping.enums.Role;
import com.example.do_shopping.exception.custom.BusinessException;
import com.example.do_shopping.exception.custom.DataNotFoundException;
import com.example.do_shopping.repository.CustomerRepository;
import com.example.do_shopping.repository.UserRepository;
import com.example.do_shopping.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public Customer register(CustomerRegisterRequestDTO request) {
        Optional<User> checkUserName = userRepository.findByUsernameAndDeletedAtIsNull(request.getUsername());

        if (checkUserName.isPresent()) {
            throw new BusinessException("Username already registered");
        }

        Optional<User> checkUserEmail = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail());

        if (checkUserEmail.isPresent()) {
            throw new BusinessException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setIsActive(1);

        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());

        return customerRepository.save(customer);
    }

    public Map<String, String> login(UserLoginRequestDTO request) {
        String identifier = request.getUsername();

        User user = userRepository
                .findByUsernameAndIsActiveAndDeletedAtIsNullOrEmailAndIsActiveAndDeletedAtIsNull(identifier, 1,
                        identifier, 1)
                .orElseThrow(() -> new BusinessException("Invalid username or password!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid username or password!");
        }

        return java.util.Map.of(
                "token", jwtUtil.generateToken(user.getUsername(), user.getRole().name()),
                "role", user.getRole().name());
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    public Customer getCurrentCustomer() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        return customerRepository
                .findByUserIdAndDeletedAtIsNull(user.getId())
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
    }

}
