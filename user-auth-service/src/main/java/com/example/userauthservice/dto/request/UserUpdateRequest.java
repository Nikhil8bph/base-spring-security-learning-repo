package com.example.userauthservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UserUpdateRequest", description = "Optional profile fields to update")
public class UserUpdateRequest {

    @Schema(description = "New username", example = "alex.morgan")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username can only contain alphanumeric characters, dots, underscores, and hyphens"
    )
    private String username;

    @Schema(description = "New email address", example = "alex@example.com")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "New mobile number in E.164 format", example = "+14155552671")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid mobile number format (must follow E.164, e.g., +1234567890)"
    )
    private String mobile;
}
