package com.example.userauthservice.resources;

import com.example.sharedkernel.dto.response.PagedResponse;
import com.example.sharedkernel.dto.response.StandardResponse;
import com.example.userauthservice.dto.response.AdminUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.List;

@Tag(name = "Admin users", description = "Administrative user search operations")
public interface AdminUserResources {
    @GetMapping("/users")
    @Operation(summary = "Search users including administrative fields")
    StandardResponse<PagedResponse<List<AdminUserResponse>>> searchAllUsers(
            @Parameter(description = "Text to search for") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Search field: ID, EMAIL, USERNAME, or MOBILE") @RequestParam(required = false) String searchType,
            @Parameter(description = "Inclusive lower bound for creation time") @RequestParam(required = false) Calendar minCreatedAt,
            @Parameter(description = "Inclusive upper bound for creation time") @RequestParam(required = false) Calendar maxCreatedAt,
            @Parameter(description = "Inclusive lower bound for last update time") @RequestParam(required = false) Calendar minLastUpdatedAt,
            @Parameter(description = "Inclusive upper bound for last update time") @RequestParam(required = false) Calendar maxLastUpdatedAt,
            @Parameter(description = "Inclusive lower bound for deletion time") @RequestParam(required = false) Calendar minDeletedAt,
            @Parameter(description = "Inclusive upper bound for deletion time") @RequestParam(required = false) Calendar maxDeletedAt,
            @Parameter(description = "Creator to filter by") @RequestParam(required = false) String createdBy,
            @Parameter(description = "Last updater to filter by") @RequestParam(required = false) String lastUpdatedBy,
            @Parameter(description = "Actor who deleted the user") @RequestParam(required = false) String deletedBy,
            @Parameter(description = "Whether the user is active") @RequestParam(defaultValue = "false") boolean active,
            @Parameter(description = "Whether the user is deleted") @RequestParam(defaultValue = "false") boolean deleted,
            @Parameter(description = "Maximum number of results", example = "20") @RequestParam(defaultValue = "20") int pageSize,
            @Parameter(description = "Zero-based page index", example = "0") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Property used to sort results", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC") @RequestParam(defaultValue = "ASC") String sortDir);
}
