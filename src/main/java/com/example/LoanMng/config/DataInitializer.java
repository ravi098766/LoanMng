package com.example.LoanMng.config;

import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!customerRepository.existsByEmailIgnoreCase("admin@loanmng.com")) {
            Customer admin = Customer.builder()
                    .fullName("System Administrator")
                    .email("admin@loanmng.com")
                    .mobile("9999999999")
                    .address("Main Admin Office")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .build();
            customerRepository.save(admin);
            System.out.println(">>> Admin account initialized: admin@loanmng.com / admin123");
        }
    }
}
