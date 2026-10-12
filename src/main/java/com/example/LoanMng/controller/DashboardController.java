package com.example.LoanMng.controller;

import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.entity.Loan;
import com.example.LoanMng.entity.Payment;
import com.example.LoanMng.service.LoanService;
import com.example.LoanMng.service.PaymentService;
import com.example.LoanMng.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final UserService userService;
    private final LoanService loanService;
    private final PaymentService paymentService;

    public DashboardController(UserService userService, LoanService loanService, PaymentService paymentService) {
        this.userService = userService;
        this.loanService = loanService;
        this.paymentService = paymentService;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";

        String identifier = authentication.getName();
        Customer customer = userService.findByIdentifier(identifier).orElse(null);
        if (customer == null) return "redirect:/login";

        boolean isAdmin = customer.getRole() != null && customer.getRole().toUpperCase().contains("ADMIN");

        List<Loan> customerLoans = isAdmin
                ? loanService.getAllLoans()
                : loanService.getLoansForCustomer(customer);

        List<Payment> recentPayments = isAdmin
                ? paymentService.getAllPayments()
                : paymentService.getPaymentsForCustomer(customer);

        long activeLoansCount = customerLoans.stream()
                .filter(l -> "APPROVED".equalsIgnoreCase(l.getStatus()))
                .count();

        double totalOutstanding = customerLoans.stream()
                .filter(l -> "APPROVED".equalsIgnoreCase(l.getStatus()))
                .mapToDouble(l -> l.getOutstandingAmount() != null ? l.getOutstandingAmount() : 0.0)
                .sum();

        double nextEmi = customerLoans.stream()
                .filter(l -> "APPROVED".equalsIgnoreCase(l.getStatus()))
                .mapToDouble(l -> l.getEmiAmount() != null ? l.getEmiAmount() : 0.0)
                .findFirst()
                .orElse(0.0);

        model.addAttribute("currentUser", customer);
        model.addAttribute("username", identifier);
        model.addAttribute("roles", authentication.getAuthorities());
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("loans", customerLoans);
        model.addAttribute("recentPayments", recentPayments);
        model.addAttribute("activeLoansCount", activeLoansCount);
        model.addAttribute("totalOutstanding", totalOutstanding);
        model.addAttribute("nextEmi", nextEmi);

        return "dashboard";
    }
}
