package com.example.userauthservice.resources;

import com.example.sharedkernel.dto.response.StandardResponse;
import com.example.userauthservice.dto.request.CreateRoleRequest;
import com.example.userauthservice.dto.request.UpdateRoleRequest;
import com.example.userauthservice.dto.response.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Roles", description = "Role management operations")
public interface RoleResources {
    @GetMapping()
    @Operation(summary = "List roles")
    StandardResponse<Set<RoleResponse>> getRoles();

    @GetMapping("/{id}")
    @Operation(summary = "Get a role")
    StandardResponse<RoleResponse> getRole(
            @Parameter(description = "Role identifier", example = "3")
            @PathVariable
            @Valid
            Long id);

    @PostMapping("/create")
    @Operation(summary = "Create a role")
    StandardResponse<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest createRoleRequest);

    @PutMapping("/update/{id}")
    @Operation(summary = "Update a role")
    StandardResponse<RoleResponse> updateRole(
            @Parameter(description = "Role identifier", example = "3")
            @PathVariable("id")
            @Valid
            Long id,
            @Valid @RequestBody UpdateRoleRequest updateRoleRequest);

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Soft-delete a role")
    StandardResponse<Void> deleteRole(
            @Parameter(description = "Role identifier", example = "3")
            @PathVariable("id")
            @Valid
            Long id);
}
