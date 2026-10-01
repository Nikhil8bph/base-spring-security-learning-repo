package com.example.userauthservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "UserResponse", description = "Public user profile and audit information")
public class UserResponse {
    @Schema(description = "User identifier", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Username", example = "alex.morgan")
    private String username;
    @Schema(description = "Email address", example = "alex@example.com")
    private String email;
    @Schema(description = "Mobile number", example = "+14155552671")
    private String mobile;
    @Schema(description = "Creator", example = "system", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;
    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private Calendar createdAt;
    @Schema(description = "Last updater", example = "system", accessMode = Schema.AccessMode.READ_ONLY)
    private String lastUpdatedBy;
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private Calendar lastUpdatedAt;
    @Schema(description = "Whether the account is active", example = "true")
    private Boolean active;
}
