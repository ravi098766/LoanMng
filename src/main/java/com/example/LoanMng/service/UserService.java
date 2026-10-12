package com.example.LoanMng.service;

import com.example.LoanMng.dto.UserRegistrationDto;
import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailExists(String email) {
        if (email == null) return false;
        return customerRepository.existsByEmailIgnoreCase(email.trim());
    }

    public boolean mobileExists(String mobile) {
        if (mobile == null) return false;
        return customerRepository.existsByMobile(mobile.trim());
    }

    public Optional<Customer> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return customerRepository.findByEmailIgnoreCase(email.trim());
    }

    public Optional<Customer> findByMobile(String mobile) {
        if (mobile == null) return Optional.empty();
        return customerRepository.findByMobile(mobile.trim());
    }

    public Optional<Customer> findByIdentifier(String identifier) {
        if (identifier == null) return Optional.empty();
        String clean = identifier.trim();
        return customerRepository.findByEmailIgnoreCaseOrMobile(clean, clean);
    }

    @Transactional
    public Customer registerCustomer(UserRegistrationDto dto) {
        if (emailExists(dto.getEmail())) {
            throw new IllegalArgumentException("Email address is already registered");
        }
        if (mobileExists(dto.getMobile())) {
            throw new IllegalArgumentException("Mobile number is already registered");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String assignedRole = dto.getRole();
        if (assignedRole == null || assignedRole.isBlank()) {
            assignedRole = "CUSTOMER";
        }

        Customer customer = Customer.builder()
                .fullName(dto.getFullName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .mobile(dto.getMobile().trim())
                .address(dto.getAddress().trim())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(assignedRole)
                .build();

        return customerRepository.save(customer);
    }
}
