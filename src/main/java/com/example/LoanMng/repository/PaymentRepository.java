package com.example.LoanMng.repository;

import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.entity.Loan;
import com.example.LoanMng.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCustomerOrderByPaymentDateDesc(Customer customer);

    List<Payment> findByCustomerIdOrderByPaymentDateDesc(Long customerId);

    List<Payment> findByLoanOrderByPaymentDateDesc(Loan loan);

    List<Payment> findByLoanIdOrderByPaymentDateDesc(Long loanId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findAllByOrderByPaymentDateDesc();
}
