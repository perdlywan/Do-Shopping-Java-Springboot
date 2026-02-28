package com.example.do_shopping.service;

import com.example.do_shopping.dto.request.CustomerRegisterRequestDTO;
import com.example.do_shopping.dto.request.UserLoginRequestDTO;
import com.example.do_shopping.entity.Customer;
import com.example.do_shopping.entity.User;
import com.example.do_shopping.enums.Role;
import com.example.do_shopping.exception.custom.BusinessException;
import com.example.do_shopping.repository.CustomerRepository;
import com.example.do_shopping.repository.UserRepository;
import com.example.do_shopping.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void register(CustomerRegisterRequestDTO request){
        Optional<User> checkUserName = userRepository.findByUsername(request.getUsername());

        if(checkUserName.isPresent()){
            throw new BusinessException("Username sudah terdaftar");
        }

        Optional<User> checkUserEmail = userRepository.findByEmail(request.getEmail());

        if(checkUserEmail.isPresent()){
            throw new BusinessException("Email sudah terdaftar");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setIs_active(1);

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());

        userRepository.save(user);
        customerRepository.save(customer);
    }

    public String login(UserLoginRequestDTO request){
        String identifier = request.getUsername();

        User user = userRepository
                .findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new BusinessException("Username atau password salah!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BusinessException("Username atau password salah!");
        }

        return jwtUtil.generateToken(request.getUsername());
    }

}
