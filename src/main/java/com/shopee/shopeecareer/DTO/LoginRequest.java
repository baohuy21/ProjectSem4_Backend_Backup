package com.shopee.shopeecareer.DTO;

import com.shopee.shopeecareer.Exception.BadRequestException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    private String captchaResponse;

    public void validateRequest() {
        // Email không được để trống
        if(email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }

        // Password không được để trống
        if(password == null || password.trim().isEmpty()) {
            throw new BadRequestException("Password cannot be empty");
        }

        // Email không đúng định dạng
        String emailRegex = "^[A-Za-z0-9.,!@#$%^&*()_-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            throw new BadRequestException("Invalid email format");
        }
    }
}
