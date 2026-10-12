package com.example.LoanMng.controller;

import com.example.LoanMng.dto.LoanApplicationDto;
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
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final PaymentService paymentService;
    private final UserService userService;

    public LoanController(LoanService loanService, PaymentService paymentService, UserService userService) {
        this.loanService = loanService;
        this.paymentService = paymentService;
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
    public String listLoans(Authentication authentication, Model model) {
        Customer customer = getCurrentCustomer(authentication);
        if (customer == null) return "redirect:/login";

        List<Loan> loans = isAdmin(customer)
                ? loanService.getAllLoans()
                : loanService.getLoansForCustomer(customer);

        model.addAttribute("loans", loans);
        model.addAttribute("currentUser", customer);
        model.addAttribute("isAdmin", isAdmin(customer));
        return "loans/list";
    }

    @GetMapping("/apply")
    public String showApplyForm(Authentication authentication, Model model) {
        Customer customer = getCurrentCustomer(authentication);
        if (customer == null) return "redirect:/login";

        if (!model.containsAttribute("loanDto")) {
            LoanApplicationDto dto = new LoanApplicationDto();
            dto.setAmount(50000.0);
            dto.setTenureMonths(24);
            model.addAttribute("loanDto", dto);
        }

        model.addAttribute("currentUser", customer);
        return "loans/apply";
    }

    @PostMapping("/apply")
    public String processApply(
            @Valid @ModelAttribute("loanDto") LoanApplicationDto loanDto,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Customer customer = getCurrentCustomer(authentication);
        if (customer == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", customer);
            return "loans/apply";
        }

        try {
            Loan newLoan = loanService.applyLoan(customer, loanDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Loan application #" + newLoan.getLoanNumber() + " submitted successfully!");
            return "redirect:/loans/" + newLoan.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error processing application: " + e.getMessage());
            model.addAttribute("currentUser", customer);
            return "loans/apply";
        }
    }

    @GetMapping("/{id}")
    public String viewLoanDetails(
            @PathVariable("id") Long id,
            Authentication authentication,
            Model model) {

        Customer customer = getCurrentCustomer(authentication);
        if (customer == null) return "redirect:/login";

        Loan loan = loanService.getLoanById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + id));

        if (!loan.getCustomer().getId().equals(customer.getId()) && !isAdmin(customer)) {
            return "redirect:/loans";
        }

        List<Payment> payments = paymentService.getPaymentsForLoan(id);

        model.addAttribute("loan", loan);
        model.addAttribute("payments", payments);
        model.addAttribute("currentUser", customer);
        model.addAttribute("isAdmin", isAdmin(customer));
        return "loans/details";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Customer customer = getCurrentCustomer(authentication);
        if (customer == null || !isAdmin(customer)) {
            return "redirect:/loans";
        }

        loanService.updateLoanStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Loan status updated to " + status);
        return "redirect:/loans/" + id;
    }
}
