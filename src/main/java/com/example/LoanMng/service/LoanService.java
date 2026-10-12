package com.example.LoanMng.service;

import com.example.LoanMng.dto.LoanApplicationDto;
import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.entity.Loan;
import com.example.LoanMng.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public double getDefaultInterestRate(String loanType) {
        if (loanType == null) return 10.0;
        return switch (loanType.trim().toLowerCase()) {
            case "home loan" -> 8.5;
            case "auto loan", "vehicle loan" -> 9.0;
            case "education loan" -> 7.5;
            case "business loan" -> 11.5;
            default -> 10.0; // Personal Loan
        };
    }

    public double calculateEmi(double principal, double annualRatePercent, int tenureMonths) {
        if (principal <= 0 || tenureMonths <= 0) return 0.0;
        if (annualRatePercent <= 0) {
            return BigDecimal.valueOf(principal / tenureMonths).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }

        double monthlyRate = (annualRatePercent / 12.0) / 100.0;
        double factor = Math.pow(1.0 + monthlyRate, tenureMonths);
        double emi = (principal * monthlyRate * factor) / (factor - 1.0);

        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Transactional
    public Loan applyLoan(Customer customer, LoanApplicationDto dto) {
        double rate = dto.getCustomInterestRate() != null && dto.getCustomInterestRate() > 0
                ? dto.getCustomInterestRate()
                : getDefaultInterestRate(dto.getLoanType());

        double emi = calculateEmi(dto.getAmount(), rate, dto.getTenureMonths());
        double totalRepayable = BigDecimal.valueOf(emi * dto.getTenureMonths()).setScale(2, RoundingMode.HALF_UP).doubleValue();

        // Generate unique loan number like LN105, LN106...
        long nextIdSuffix = loanRepository.count() + 101;
        String loanNumber = "LN" + nextIdSuffix;

        Loan loan = Loan.builder()
                .loanNumber(loanNumber)
                .loanType(dto.getLoanType())
                .amount(dto.getAmount())
                .interestRate(rate)
                .tenureMonths(dto.getTenureMonths())
                .emiAmount(emi)
                .remainingEmi(dto.getTenureMonths())
                .outstandingAmount(totalRepayable)
                .status("APPROVED") // Auto-approve so customers can immediately manage and pay EMIs
                .appliedDate(LocalDate.now())
                .customer(customer)
                .build();

        return loanRepository.save(loan);
    }

    public List<Loan> getLoansForCustomer(Customer customer) {
        return loanRepository.findByCustomerOrderByAppliedDateDesc(customer);
    }

    public List<Loan> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerIdOrderByAppliedDateDesc(customerId);
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAllByOrderByAppliedDateDesc();
    }

    public List<Loan> getLoansByStatus(String status) {
        return loanRepository.findByStatusOrderByAppliedDateDesc(status);
    }

    @Transactional
    public Loan updateLoanStatus(Long loanId, String newStatus) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));
        loan.setStatus(newStatus.toUpperCase());
        return loanRepository.save(loan);
    }

    public Double getTotalOutstandingForCustomer(Long customerId) {
        return loanRepository.sumOutstandingAmountByCustomerId(customerId);
    }

    public long getActiveLoansCountForCustomer(Long customerId) {
        return loanRepository.countActiveLoansByCustomerId(customerId);
    }
}
