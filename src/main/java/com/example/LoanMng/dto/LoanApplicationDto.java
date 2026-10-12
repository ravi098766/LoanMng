package com.example.LoanMng.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoanApplicationDto {

    @NotBlank(message = "Loan type is required")
    private String loanType = "Personal Loan";

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000.0", message = "Minimum loan amount is 1,000")
    private Double amount;

    @NotNull(message = "Tenure is required")
    @Min(value = 3, message = "Minimum tenure is 3 months")
    @Max(value = 360, message = "Maximum tenure is 360 months")
    private Integer tenureMonths;

    private Double customInterestRate;

    public LoanApplicationDto() {
    }

    public LoanApplicationDto(String loanType, Double amount, Integer tenureMonths) {
        this.loanType = loanType;
        this.amount = amount;
        this.tenureMonths = tenureMonths;
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

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public Double getCustomInterestRate() {
        return customInterestRate;
    }

    public void setCustomInterestRate(Double customInterestRate) {
        this.customInterestRate = customInterestRate;
    }
}
