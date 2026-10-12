package com.example.LoanMng.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number")
    private String loanNumber;

    @Column(name = "loan_type")
    private String loanType;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "tenure_months")
    private Integer tenureMonths;

    @Column(name = "emi_amount")
    private Double emiAmount;

    @Column(name = "remaining_emi")
    private Integer remainingEmi;

    @Column(name = "outstanding_amount")
    private Double outstandingAmount;

    @Column(name = "status")
    private String status; // "PENDING", "APPROVED", "REJECTED", "CLOSED"

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Loan() {
    }

    public Loan(Long id, String loanNumber, String loanType, Double amount, Double interestRate, Integer tenureMonths, Double emiAmount, Integer remainingEmi, Double outstandingAmount, String status, LocalDate appliedDate, Customer customer) {
        this.id = id;
        this.loanNumber = loanNumber;
        this.loanType = loanType;
        this.amount = amount;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
        this.emiAmount = emiAmount;
        this.remainingEmi = remainingEmi;
        this.outstandingAmount = outstandingAmount;
        this.status = status;
        this.appliedDate = appliedDate;
        this.customer = customer;
    }

    public static LoanBuilder builder() {
        return new LoanBuilder();
    }

    public static class LoanBuilder {
        private Long id;
        private String loanNumber;
        private String loanType;
        private Double amount;
        private Double interestRate;
        private Integer tenureMonths;
        private Double emiAmount;
        private Integer remainingEmi;
        private Double outstandingAmount;
        private String status = "PENDING";
        private LocalDate appliedDate;
        private Customer customer;

        LoanBuilder() {
        }

        public LoanBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LoanBuilder loanNumber(String loanNumber) {
            this.loanNumber = loanNumber;
            return this;
        }

        public LoanBuilder loanType(String loanType) {
            this.loanType = loanType;
            return this;
        }

        public LoanBuilder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public LoanBuilder interestRate(Double interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public LoanBuilder tenureMonths(Integer tenureMonths) {
            this.tenureMonths = tenureMonths;
            return this;
        }

        public LoanBuilder emiAmount(Double emiAmount) {
            this.emiAmount = emiAmount;
            return this;
        }

        public LoanBuilder remainingEmi(Integer remainingEmi) {
            this.remainingEmi = remainingEmi;
            return this;
        }

        public LoanBuilder outstandingAmount(Double outstandingAmount) {
            this.outstandingAmount = outstandingAmount;
            return this;
        }

        public LoanBuilder status(String status) {
            this.status = status;
            return this;
        }

        public LoanBuilder appliedDate(LocalDate appliedDate) {
            this.appliedDate = appliedDate;
            return this;
        }

        public LoanBuilder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Loan build() {
            return new Loan(id, loanNumber, loanType, amount, interestRate, tenureMonths, emiAmount, remainingEmi, outstandingAmount, status, appliedDate, customer);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public Double getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(Double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public Integer getRemainingEmi() {
        return remainingEmi;
    }

    public void setRemainingEmi(Integer remainingEmi) {
        this.remainingEmi = remainingEmi;
    }

    public Double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(Double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
