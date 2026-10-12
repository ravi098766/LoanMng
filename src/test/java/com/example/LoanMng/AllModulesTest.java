package com.example.LoanMng;

import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.entity.Loan;
import com.example.LoanMng.entity.Payment;
import com.example.LoanMng.repository.CustomerRepository;
import com.example.LoanMng.repository.LoanRepository;
import com.example.LoanMng.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AllModulesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Customer testCustomer;
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findByEmailIgnoreCase("moduletest@example.com")
                .orElseGet(() -> customerRepository.save(
                        Customer.builder()
                                .fullName("Module Tester")
                                .email("moduletest@example.com")
                                .mobile("9876543299")
                                .address("Module Test Address")
                                .password(passwordEncoder.encode("pass123"))
                                .role("ROLE_CUSTOMER")
                                .build()
                ));

        testLoan = loanRepository.findByCustomerOrderByAppliedDateDesc(testCustomer)
                .stream()
                .filter(l -> "APPROVED".equals(l.getStatus()))
                .findFirst()
                .orElseGet(() -> loanRepository.save(
                        Loan.builder()
                                .loanNumber("LN-TEST-" + System.currentTimeMillis())
                                .loanType("Personal Loan")
                                .amount(50000.0)
                                .interestRate(10.0)
                                .tenureMonths(12)
                                .emiAmount(4395.79)
                                .remainingEmi(12)
                                .outstandingAmount(52749.48)
                                .status("APPROVED")
                                .appliedDate(LocalDate.now())
                                .customer(testCustomer)
                                .build()
                ));
    }

    @Test
    void testCalculatorPageAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/calculator"))
                .andExpect(status().isOk())
                .andExpect(view().name("calculator"));
    }

    @Test
    @WithMockUser(username = "moduletest@example.com", roles = "CUSTOMER")
    void testDashboardPageAccessibleWithAuth() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("loans"))
                .andExpect(model().attributeExists("recentPayments"))
                .andExpect(model().attributeExists("totalOutstanding"));
    }

    @Test
    @WithMockUser(username = "moduletest@example.com", roles = "CUSTOMER")
    void testLoansListPage() throws Exception {
        mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andExpect(view().name("loans/list"))
                .andExpect(model().attributeExists("loans"));
    }

    @Test
    @WithMockUser(username = "moduletest@example.com", roles = "CUSTOMER")
    void testLoanApplyGet() throws Exception {
        mockMvc.perform(get("/loans/apply"))
                .andExpect(status().isOk())
                .andExpect(view().name("loans/apply"))
                .andExpect(model().attributeExists("loanDto"));
    }

    @Test
    @WithMockUser(username = "moduletest@example.com", roles = "CUSTOMER")
    void testLoanApplyPost() throws Exception {
        mockMvc.perform(post("/loans/apply")
                        .with(csrf())
                        .param("loanType", "Auto Loan")
                        .param("amount", "25000")
                        .param("tenureMonths", "12"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", notNullValue()));
    }

    @Test
    @WithMockUser(username = "moduletest@example.com", roles = "CUSTOMER")
    void testLoanDetailsPage() throws Exception {
        mockMvc.perform(get("/loans/" + testLoan.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("loans/details"))
                .andExpect(model().attributeExists("loan"))
                .andExpect(model().attributeExists("payments"));
    }

    @Test
    @WithMockUser(username = "moduletest@example.com", roles = "CUSTOMER")
    void testPaymentsListPage() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(view().name("payments/list"))
                .andExpect(model().attributeExists("payments"));
    }

    @Test
    @WithMockUser(username = "moduletest@example.com", roles = "CUSTOMER")
    void testPaymentPayGet() throws Exception {
        mockMvc.perform(get("/payments/pay/" + testLoan.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("payments/pay"))
                .andExpect(model().attributeExists("loan"))
                .andExpect(model().attributeExists("paymentDto"));
    }

    @Test
    @WithMockUser(username = "moduletest@example.com", roles = "CUSTOMER")
    void testPaymentPayPostAndReceipt() throws Exception {
        mockMvc.perform(post("/payments/pay")
                        .with(csrf())
                        .param("loanId", testLoan.getId().toString())
                        .param("amount", "1000.00")
                        .param("paymentMethod", "UPI"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/payments/receipt/*"));
    }
}
