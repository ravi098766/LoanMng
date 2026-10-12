package com.example.LoanMng.service;

import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.entity.Loan;
import com.example.LoanMng.entity.Payment;
import com.example.LoanMng.repository.LoanRepository;
import com.example.LoanMng.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;

    public PaymentService(PaymentRepository paymentRepository, LoanRepository loanRepository) {
        this.paymentRepository = paymentRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public Payment processPayment(Long loanId, Customer customer, Double amount, String paymentMethod) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));

        if (!loan.getCustomer().getId().equals(customer.getId()) && !customer.getRole().toUpperCase().contains("ADMIN")) {
            throw new IllegalArgumentException("You are not authorized to make payments for this loan");
        }

        if ("CLOSED".equalsIgnoreCase(loan.getStatus())) {
            throw new IllegalArgumentException("This loan is already fully repaid and closed");
        }

        if ("REJECTED".equalsIgnoreCase(loan.getStatus())) {
            throw new IllegalArgumentException("Cannot pay for a rejected loan");
        }

        double payAmount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
        String txnId = "TXN" + System.currentTimeMillis();

        Payment payment = Payment.builder()
                .transactionId(txnId)
                .amount(payAmount)
                .paymentDate(LocalDateTime.now())
                .paymentMethod(paymentMethod != null && !paymentMethod.isBlank() ? paymentMethod : "UPI")
                .status("SUCCESS")
                .customer(loan.getCustomer())
                .loan(loan)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update Loan remaining balance and EMIs
        double newOutstanding = Math.max(0.0, loan.getOutstandingAmount() - payAmount);
        int newRemainingEmi = Math.max(0, loan.getRemainingEmi() - 1);

        if (newOutstanding <= 0.01 || newRemainingEmi == 0) {
            loan.setOutstandingAmount(0.0);
            loan.setRemainingEmi(0);
            loan.setStatus("CLOSED");
        } else {
            loan.setOutstandingAmount(BigDecimal.valueOf(newOutstanding).setScale(2, RoundingMode.HALF_UP).doubleValue());
            loan.setRemainingEmi(newRemainingEmi);
        }

        loanRepository.save(loan);

        return savedPayment;
    }

    public List<Payment> getPaymentsForCustomer(Customer customer) {
        return paymentRepository.findByCustomerOrderByPaymentDateDesc(customer);
    }

    public List<Payment> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerIdOrderByPaymentDateDesc(customerId);
    }

    public List<Payment> getPaymentsForLoan(Long loanId) {
        return paymentRepository.findByLoanIdOrderByPaymentDateDesc(loanId);
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentDateDesc();
    }
}
