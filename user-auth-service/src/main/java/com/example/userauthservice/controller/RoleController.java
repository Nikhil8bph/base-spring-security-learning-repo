package com.example.userauthservice.controller;

import com.example.sharedkernel.constants.StatusCode;
import com.example.sharedkernel.dto.response.StandardResponse;
import com.example.userauthservice.dto.request.CreateRoleRequest;
import com.example.userauthservice.dto.request.UpdateRoleRequest;
import com.example.userauthservice.dto.response.RoleResponse;
import com.example.userauthservice.resources.RoleResources;
import com.example.userauthservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController implements RoleResources {
    private final RoleService roleService;

    @Override
    public StandardResponse<Set<RoleResponse>> getRoles() {
        var roles = roleService.getRoles();
        return StandardResponse
                .<Set<RoleResponse>>builder()
                .data(roles)
                .code(StatusCode.SUCCESS)
                .message("Roles fetched successfully")
                .build();
    }

    @Override
    public StandardResponse<RoleResponse> getRole(Long id) {
        var role = roleService.getRole(id);
        return StandardResponse.
                <RoleResponse>builder()
                .data(role)
                .code(StatusCode.SUCCESS)
                .message("Role fetched successfully")
                .build();
    }

    @Override
    public StandardResponse<RoleResponse> createRole(CreateRoleRequest createRoleRequest) {
        var role = roleService.createRole(createRoleRequest);
        return StandardResponse.
                <RoleResponse>builder()
                .data(role)
                .code(StatusCode.CREATED)
                .message("Role created successfully")
                .build();
    }

    @Override
    public StandardResponse<RoleResponse> updateRole(Long id, UpdateRoleRequest updateRoleRequest) {
        var role = roleService.updateRole(id, updateRoleRequest);
        return StandardResponse.
                <RoleResponse>builder()
                .data(role)
                .code(StatusCode.SUCCESS)
                .message("Role updated successfully")
                .build();
    }

    @Override
    public StandardResponse<Void> deleteRole(Long id) {
        roleService.deleteRole(id);
        return StandardResponse
                .<Void>builder()
                .code(StatusCode.NO_CONTENT)
                .message("Role deleted successfully")
                .build();
    }
}
