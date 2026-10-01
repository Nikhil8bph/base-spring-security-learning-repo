package com.example.userauthservice.service.impl;

import com.example.userauthservice.dto.RoleDTO;
import com.example.userauthservice.dto.request.CreateRoleRequest;
import com.example.userauthservice.dto.request.UpdateRoleRequest;
import com.example.userauthservice.dto.response.RoleResponse;
import com.example.userauthservice.facade.RoleFacade;
import com.example.userauthservice.mapper.RoleRequestMapper;
import com.example.userauthservice.mapper.RoleResponseMapper;
import com.example.userauthservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleFacade roleFacade;
    private final RoleRequestMapper roleRequestMapper;
    private final RoleResponseMapper roleResponseMapper;

    @Override
    public RoleResponse createRole(CreateRoleRequest createRoleRequest) {
        RoleDTO roleDTO = roleRequestMapper.toDTO(createRoleRequest);
        RoleDTO savedRole = roleFacade.createRole(roleDTO);
        return roleResponseMapper.toResponse(savedRole);
    }

    @Override
    public RoleResponse updateRole(Long roleId, UpdateRoleRequest updateRoleRequest) {
        RoleDTO roleDTO = roleRequestMapper.toUpdateDTO(updateRoleRequest);
        RoleDTO updatedRole = roleFacade.updateRole(roleId, roleDTO);
        return roleResponseMapper.toResponse(updatedRole);
    }

    @Override
    public RoleResponse getRole(Long roleId) {
        RoleDTO roleDTO = roleFacade.getRole(roleId);
        return roleResponseMapper.toResponse(roleDTO);
    }

    @Override
    public void deleteRole(Long roleId) {
        roleFacade.deleteRole(roleId);
    }

    @Override
    public Set<RoleResponse> getRoles() {
        Set<RoleDTO> roleDTOs = roleFacade.getRoles();
        return roleResponseMapper.toResponses(roleDTOs);
    }
}
