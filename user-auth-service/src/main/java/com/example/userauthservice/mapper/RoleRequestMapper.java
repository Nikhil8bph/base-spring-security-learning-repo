package com.example.userauthservice.mapper;

import com.example.userauthservice.dto.RoleDTO;
import com.example.userauthservice.dto.request.CreateRoleRequest;
import com.example.userauthservice.dto.request.UpdateRoleRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleRequestMapper {
    RoleDTO toDTO(CreateRoleRequest createRoleRequest);

    String toRoleName(RoleDTO roleDTO);

    RoleDTO toUpdateDTO(UpdateRoleRequest updateRoleRequest);
}
