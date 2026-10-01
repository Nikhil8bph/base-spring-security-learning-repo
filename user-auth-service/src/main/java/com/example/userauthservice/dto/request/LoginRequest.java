package com.example.userauthservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "LoginRequest", description = "Credentials and identifier for a login request")
public class LoginRequest {

    @Schema(description = "Email address associated with the account (required when signUpUsing is EMAIL)", example = "alex@example.com")
    private String email;

    @Schema(description = "Account password", example = "StrongPass123!", accessMode = Schema.AccessMode.WRITE_ONLY)
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    private String password;

    @Schema(description = "Mobile number in E.164 format (required when signUpUsing is MOBILE)", example = "+14155552671")
    private String mobile;

    @Schema(description = "Identifier used for login", allowableValues = {"EMAIL", "MOBILE"}, example = "EMAIL")
    @NotBlank(message = "Sign up method is required")
    @Pattern(regexp = "^(?i)(EMAIL|MOBILE)$", message = "signUpUsing must be one of: EMAIL, MOBILE")
    private String signUpUsing;
}
