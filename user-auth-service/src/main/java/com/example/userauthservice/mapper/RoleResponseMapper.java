package com.example.userauthservice.mapper;

import com.example.userauthservice.dto.RoleDTO;
import com.example.userauthservice.dto.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleResponseMapper {
    @Mapping(target = "createdAt", source = "createdDate")
    @Mapping(target = "lastUpdatedBy", source = "lastModifiedBy")
    @Mapping(target = "lastUpdatedAt", source = "lastModifiedDate")
    RoleResponse toResponse(RoleDTO savedRole);

    Set<RoleResponse> toResponses(Set<RoleDTO> roleDTOs);
}
