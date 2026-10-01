package com.example.userauthservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    private String password;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid mobile number format (must follow E.164, e.g., +1234567890)"
    )
    private String mobile;

    @NotBlank(message = "Sign up method is required")
    @Pattern(
            regexp = "^(?i)(EMAIL|MOBILE)$",
            message = "signUpUsing must be one of: EMAIL, MOBILE"
    )
    private String signUpUsing;
}