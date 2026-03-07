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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public Customer register(CustomerRegisterRequestDTO request){
        Optional<User> checkUserName = userRepository.findByUsernameAndDeletedAtIsNull(request.getUsername());

        if(checkUserName.isPresent()){
            throw new BusinessException("Username sudah terdaftar");
        }

        Optional<User> checkUserEmail = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail());

        if(checkUserEmail.isPresent()){
            throw new BusinessException("Email sudah terdaftar");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setIsActive(1);

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());

        userRepository.save(user);
        return customerRepository.save(customer);
    }

    public String login(UserLoginRequestDTO request){
        String identifier = request.getUsername();

        User user = userRepository
                .findByUsernameAndIsActiveAndDeletedAtIsNullOrEmailAndIsActiveAndDeletedAtIsNull(identifier, 1, identifier, 1)
                .orElseThrow(() -> new BusinessException("Username atau password salah!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BusinessException("Username atau password salah!");
        }

        return jwtUtil.generateToken(request.getUsername());
    }

    public Customer getCurrentCustomer(){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new DataNotFoundException("User tidak ditemukan"));

        return customerRepository
                .findByUserIdAndDeletedAtIsNull(user.getId())
                .orElseThrow(() -> new DataNotFoundException("Customer tidak ditemukan"));
    }

}
