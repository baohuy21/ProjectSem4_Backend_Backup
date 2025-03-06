package com.shopee.shopeecareer.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO {
  @NotBlank(message = "Name is required")
  private String fullName;

  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  private String email;

  @Size(min = 6, message = "Password must be at least 6 characters")
  private String password;
  // private String address;
  // private String password;
  // Nhat
}
