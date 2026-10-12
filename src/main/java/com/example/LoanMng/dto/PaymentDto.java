package com.example.LoanMng.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentDto {

    @NotNull(message = "Loan ID is required")
    private Long loanId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "1.0", message = "Minimum payment amount is 1")
    private Double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod = "UPI";

    public PaymentDto() {
    }

    public PaymentDto(Long loanId, Double amount, String paymentMethod) {
        this.loanId = loanId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
