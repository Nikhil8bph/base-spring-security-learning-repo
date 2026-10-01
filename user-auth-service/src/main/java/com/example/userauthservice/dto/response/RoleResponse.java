package com.example.userauthservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
@Schema(name = "RoleResponse", description = "Role details and audit information")
public class RoleResponse {
    @Schema(description = "Role identifier", example = "3", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Creator", example = "system", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;
    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private Calendar createdAt;
    @Schema(description = "Last updater", example = "system", accessMode = Schema.AccessMode.READ_ONLY)
    private String lastUpdatedBy;
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private Calendar lastUpdatedAt;
    @Schema(description = "Whether the role is active", example = "true")
    private Boolean active;
    @Schema(description = "Role name", example = "ROLE_USER")
    private String name;
    @Schema(description = "Purpose of the role", example = "Standard application user")
    private String description;
    @Schema(description = "Whether this role is assigned to new users by default", example = "true")
    private Boolean defaultRole;
}
