package com.example.userauthservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UpdateRoleRequest", description = "Role fields to update")
public class UpdateRoleRequest {

    @Schema(description = "Role name", example = "ROLE_USER", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Purpose of the role", example = "Standard application user", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "Whether new users receive this role by default", example = "true")
    private boolean defaultRole;
}
