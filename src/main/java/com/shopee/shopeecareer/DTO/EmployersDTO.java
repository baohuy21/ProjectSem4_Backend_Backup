package com.shopee.shopeecareer.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopee.shopeecareer.Exception.BadRequestException;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployersDTO {
//    private String employerNumber;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @JsonIgnore
    private String password;

    @NotBlank(message = "Phone Number is required")
    private String phoneNumber;

    @NotBlank(message = "Job Title is required")
    private String jobTitle;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Role is required")
    private String role;

    @Transient
    @JsonIgnore
    private MultipartFile photo;

    private Boolean isFirstLogin;
    private Boolean isPasswordChanged;

    private LocalDateTime passwordExpiryTime;

    public void validateEmployerData() {
        // First name khong duoc de trong
        if(firstName == null || firstName.trim().isEmpty()) {
            throw new BadRequestException("First name cannot be empty");
        }

        if(firstName.matches(".*\\d.*")) {
            throw new BadRequestException("Fullname cannot contain numbers");
        }

        // so dien thoai khong duoc de trong
        if(phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new BadRequestException("Phone number cannot be empty");
        }

        // Kiểm tra độ dài nhập sdt
        if(phoneNumber.length() < 10 || phoneNumber.length() > 15) {
            throw new BadRequestException("Phone number must have 10-15 digits");
        }

        // Kiem tra phone phai la dang so
        String phonePattern = "^[0-9]{10,15}$";
        if(!phoneNumber.matches(phonePattern)) {
            throw new BadRequestException("Phone number must contain only digits");
        }

        // Email khong duoc de trong
        if(email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }

        // Kiem tra email phai dung format
        String emailRegex = "^[A-Za-z0-9.,!@#$%^&*()_-]+@(.+)$";
        if(!email.matches(emailRegex)) {
            throw new BadRequestException("Invalid email format");
        }

        // Job Title khong duoc de trong
        if(jobTitle == null || jobTitle.trim().isEmpty()) {
            throw new BadRequestException("Job title cannot be empty");
        }

        // Role khong duoc de trong
        if(role == null || role.trim().isEmpty()) {
            throw new BadRequestException("Role cannot be empty");
        }
    }
}
