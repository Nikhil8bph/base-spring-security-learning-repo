package com.example.userauthservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
@Schema(name = "AdminRoleResponse", description = "Role details with administrative deletion fields")
public class AdminRoleResponse extends RoleResponse {
    @Schema(description = "Actor who deleted the role", example = "admin", accessMode = Schema.AccessMode.READ_ONLY)
    private String deletedBy;
    @Schema(description = "Deletion timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private Calendar deletedAt;
    @Schema(description = "Whether the role has been soft-deleted", example = "false")
    private Boolean deleted;
}
