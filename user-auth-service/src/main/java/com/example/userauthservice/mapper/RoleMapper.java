package com.example.userauthservice.mapper;

import com.example.userauthservice.dto.RoleDTO;
import com.example.userauthservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface RoleMapper {
    RoleDTO toDTO(Role role);

    Role toEntity(RoleDTO roleDTO);

    Set<RoleDTO> toDTO(List<Role> roles);

    Set<RoleDTO> toDTO(Set<Role> roles);

    List<Role> toEntity(List<RoleDTO> roleDTOS);
}
