package com.example.userauthservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "SignUpRequest", description = "Information required to register a user")
public class SignUpRequest {

    @Schema(description = "User's email address", example = "alex@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Account password", example = "StrongPass123!", accessMode = Schema.AccessMode.WRITE_ONLY,
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    private String password;

    @Schema(description = "User's mobile number in E.164 format", example = "+14155552671",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid mobile number format (must follow E.164, e.g., +1234567890)"
    )
    private String mobile;

    @Schema(description = "Identifier used for sign-up", allowableValues = {"EMAIL", "MOBILE"},
            example = "EMAIL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Sign up method is required")
    @Pattern(
            regexp = "^(?i)(EMAIL|MOBILE)$",
            message = "signUpUsing must be one of: EMAIL, MOBILE"
    )
    private String signUpUsing;
}
