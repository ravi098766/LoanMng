package com.example.LoanMng;

import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        if (!customerRepository.existsByEmailIgnoreCase("testuser@example.com")) {
            Customer testUser = Customer.builder()
                    .fullName("Test User")
                    .email("testuser@example.com")
                    .mobile("9876543210")
                    .address("123 Test Street")
                    .password(passwordEncoder.encode("secret123"))
                    .role("CUSTOMER")
                    .build();
            customerRepository.save(testUser);
        }
    }

    @Test
    void testLoginPageIsAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testRegisterPageIsAccessible() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testUnauthenticatedUserRedirectedToLoginFromDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"CUSTOMER"})
    void testAuthenticatedUserCanAccessDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("username"));
    }

    @Test
    void testSuccessfulRegistration() throws Exception {
        String uniqueEmail = "newuser_" + System.currentTimeMillis() + "@example.com";
        String uniqueMobile = "9" + (System.currentTimeMillis() % 1000000000L);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "New Customer")
                        .param("email", uniqueEmail)
                        .param("mobile", uniqueMobile)
                        .param("address", "Plot 42, Green Avenue")
                        .param("password", "newpass123")
                        .param("confirmPassword", "newpass123")
                        .param("role", "CUSTOMER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));

        assertTrue(customerRepository.existsByEmailIgnoreCase(uniqueEmail));
    }

    @Test
    void testRegistrationFailsOnPasswordMismatch() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "Mismatch Customer")
                        .param("email", "mismatch@example.com")
                        .param("mobile", "9123456789")
                        .param("address", "Some Address")
                        .param("password", "pass1234")
                        .param("confirmPassword", "differentpass")
                        .param("role", "CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "testuser@example.com")
                        .param("password", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void testLoginFailureOnWrongPassword() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "testuser@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }
}
