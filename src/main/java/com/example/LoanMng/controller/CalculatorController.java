package com.example.LoanMng.controller;

import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalculatorController {

    private final UserService userService;

    public CalculatorController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/calculator")
    public String showCalculator(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            Customer customer = userService.findByIdentifier(authentication.getName()).orElse(null);
            model.addAttribute("currentUser", customer);
        }
        return "calculator";
    }
}
