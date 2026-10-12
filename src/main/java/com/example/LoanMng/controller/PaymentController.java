package com.example.LoanMng.controller;

import com.example.LoanMng.dto.PaymentDto;
import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.entity.Loan;
import com.example.LoanMng.entity.Payment;
import com.example.LoanMng.service.LoanService;
import com.example.LoanMng.service.PaymentService;
import com.example.LoanMng.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final LoanService loanService;
    private final UserService userService;

    public PaymentController(PaymentService paymentService, LoanService loanService, UserService userService) {
        this.paymentService = paymentService;
        this.loanService = loanService;
        this.userService = userService;
    }

    private Customer getCurrentCustomer(Authentication authentication) {
        if (authentication == null) return null;
        return userService.findByIdentifier(authentication.getName()).orElse(null);
    }

    private boolean isAdmin(Customer customer) {
        return customer != null && customer.getRole() != null && customer.getRole().toUpperCase().contains("ADMIN");
    }

    @GetMapping
    public String listPayments(Authentication authentication, Model model) {
        Customer customer = getCurrentCustomer(authentication);
        if (customer == null) return "redirect:/login";

        List<Payment> payments = isAdmin(customer)
                ? paymentService.getAllPayments()
                : paymentService.getPaymentsForCustomer(customer);

        model.addAttribute("payments", payments);
        model.addAttribute("currentUser", customer);
        model.addAttribute("isAdmin", isAdmin(customer));
        return "payments/list";
    }

    @GetMapping("/pay/{loanId}")
    public String showPayForm(
            @PathVariable("loanId") Long loanId,
            Authentication authentication,
            Model model) {

        Customer customer = getCurrentCustomer(authentication);
        if (customer == null) return "redirect:/login";

        Loan loan = loanService.getLoanById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));

        if (!loan.getCustomer().getId().equals(customer.getId()) && !isAdmin(customer)) {
            return "redirect:/loans";
        }

        PaymentDto dto = new PaymentDto();
        dto.setLoanId(loan.getId());
        dto.setAmount(loan.getEmiAmount());
        dto.setPaymentMethod("UPI");

        model.addAttribute("paymentDto", dto);
        model.addAttribute("loan", loan);
        model.addAttribute("currentUser", customer);
        return "payments/pay";
    }

    @PostMapping("/pay")
    public String processPay(
            @Valid @ModelAttribute("paymentDto") PaymentDto paymentDto,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Customer customer = getCurrentCustomer(authentication);
        if (customer == null) return "redirect:/login";

        Loan loan = loanService.getLoanById(paymentDto.getLoanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + paymentDto.getLoanId()));

        if (bindingResult.hasErrors()) {
            model.addAttribute("loan", loan);
            model.addAttribute("currentUser", customer);
            return "payments/pay";
        }

        try {
            Payment payment = paymentService.processPayment(
                    loan.getId(),
                    customer,
                    paymentDto.getAmount(),
                    paymentDto.getPaymentMethod()
            );
            redirectAttributes.addFlashAttribute("successMessage",
                    "Payment of ₹" + String.format("%.2f", payment.getAmount()) +
                    " successful! Transaction ID: " + payment.getTransactionId());
            return "redirect:/payments/receipt/" + payment.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("loan", loan);
            model.addAttribute("currentUser", customer);
            return "payments/pay";
        }
    }

    @GetMapping("/receipt/{id}")
    public String viewReceipt(
            @PathVariable("id") Long id,
            Authentication authentication,
            Model model) {

        Customer customer = getCurrentCustomer(authentication);
        if (customer == null) return "redirect:/login";

        Payment payment = paymentService.getPaymentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));

        if (!payment.getCustomer().getId().equals(customer.getId()) && !isAdmin(customer)) {
            return "redirect:/payments";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("currentUser", customer);
        return "payments/receipt";
    }
}
