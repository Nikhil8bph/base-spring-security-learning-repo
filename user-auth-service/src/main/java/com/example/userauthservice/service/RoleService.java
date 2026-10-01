package com.example.userauthservice.service;

import com.example.userauthservice.dto.request.CreateRoleRequest;
import com.example.userauthservice.dto.request.UpdateRoleRequest;
import com.example.userauthservice.dto.response.RoleResponse;

import java.util.Set;

public interface RoleService {
    RoleResponse createRole(CreateRoleRequest createRoleRequest);

    RoleResponse updateRole(Long roleId, UpdateRoleRequest updateRoleRequest);

    RoleResponse getRole(Long roleId);

    void deleteRole(Long roleId);

    Set<RoleResponse> getRoles();
}
