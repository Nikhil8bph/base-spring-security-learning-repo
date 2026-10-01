package com.example.userauthservice.resources;

import com.example.sharedkernel.dto.response.PagedResponse;
import com.example.sharedkernel.dto.response.StandardResponse;
import com.example.userauthservice.dto.request.LoginRequest;
import com.example.userauthservice.dto.request.SignUpRequest;
import com.example.userauthservice.dto.request.UpdatePasswordRequest;
import com.example.userauthservice.dto.request.UserUpdateRequest;
import com.example.userauthservice.dto.response.UserLoginResponse;
import com.example.userauthservice.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@Tag(name = "Users", description = "User registration, lookup, profile, and password operations")
public interface UserResources {
    @GetMapping
    @Operation(summary = "Search users", description = "Returns a filtered and paginated list of users.")
    StandardResponse<PagedResponse<List<UserResponse>>> searchAllUsers(
            @Parameter(description = "Text to search for") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Search field: ID, EMAIL, USERNAME, or MOBILE") @RequestParam(required = false) String searchType,
            @Parameter(description = "Inclusive lower bound for creation time") @RequestParam(required = false) Calendar minCreatedAt,
            @Parameter(description = "Inclusive upper bound for creation time") @RequestParam(required = false) Calendar maxCreatedAt,
            @Parameter(description = "Inclusive lower bound for last update time") @RequestParam(required = false) Calendar minLastUpdatedAt,
            @Parameter(description = "Inclusive upper bound for last update time") @RequestParam(required = false) Calendar maxLastUpdatedAt,
            @Parameter(description = "Creator to filter by") @RequestParam(required = false) String createdBy,
            @Parameter(description = "Last updater to filter by") @RequestParam(required = false) String lastUpdatedBy,
            @Parameter(description = "Whether the user is active") @RequestParam(defaultValue = "false") boolean active,
            @Parameter(description = "Maximum number of results", example = "20") @RequestParam(defaultValue = "20") int pageSize,
            @Parameter(description = "Zero-based page index", example = "0") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Property used to sort results", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC") @RequestParam(defaultValue = "ASC") String sortDir);

    @PostMapping("/signup")
    @Operation(summary = "Register a user")
    StandardResponse<UserResponse> userSignUp(@Valid @RequestBody SignUpRequest signUpRequest);

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates with an email address or mobile number and password.")
    StandardResponse<UserLoginResponse> userLogIn(@Valid @RequestBody LoginRequest loginRequest);

    @GetMapping("/{userVal}/{userIdentifier}")
    @Operation(summary = "Get a user", description = "Looks up a user using the selected identifier type.")
    StandardResponse<UserResponse> getUser(
            @Parameter(description = "Value to look up", example = "alex@example.com") @PathVariable String userVal,
            @Parameter(description = "Identifier type: ID, EMAIL, USERNAME, or MOBILE", example = "EMAIL")
            @PathVariable String userIdentifier);

    @PutMapping("/update/{userId}")
    @Operation(summary = "Update a user profile")
    StandardResponse<UserResponse> updateUser(
            @Parameter(description = "User identifier", example = "42")
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest);

    @DeleteMapping("/{userId}")
    @Operation(summary = "Soft-delete a user")
    StandardResponse<Void> deleteUser(
            @Parameter(description = "User identifier", example = "42")
            @PathVariable("userId")
            Long userId);

    @PutMapping("/update-password/{userId}")
    @Operation(summary = "Update a user's password")
    StandardResponse<UserResponse> updatePassword(
            @Parameter(description = "User identifier", example = "42")
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest);
}
