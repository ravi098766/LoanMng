package com.example.LoanMng.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9+()\\s-]{7,20}$", message = "Please enter a valid mobile number")
    private String mobile;

    @NotBlank(message = "Address is required")
    @Size(min = 3, max = 255, message = "Please enter a valid address")
    private String address;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 40, message = "Password must be at least 4 characters long")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    private String role = "CUSTOMER";

    public UserRegistrationDto() {
    }

    public UserRegistrationDto(String fullName, String email, String mobile, String address, String password, String confirmPassword, String role) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
