package com.example.LoanMng.repository;

import com.example.LoanMng.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmailIgnoreCase(String email);

    Optional<Customer> findByMobile(String mobile);

    Optional<Customer> findByEmailIgnoreCaseOrMobile(String email, String mobile);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMobile(String mobile);
}
