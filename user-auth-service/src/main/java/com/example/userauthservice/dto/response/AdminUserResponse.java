package com.example.userauthservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "AdminUserResponse", description = "User profile with administrative and deletion fields")
public class AdminUserResponse extends UserResponse {
    @Schema(description = "Actor who deleted the user", example = "admin", accessMode = Schema.AccessMode.READ_ONLY)
    private String deletedBy;
    @Schema(description = "Deletion timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private Calendar deletedDate;
    @Schema(description = "Whether the user has been soft-deleted", example = "false")
    private Boolean deleted;
}
