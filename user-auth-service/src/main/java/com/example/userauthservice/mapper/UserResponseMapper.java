package com.example.userauthservice.mapper;

import com.example.sharedkernel.dto.PageDTO;
import com.example.sharedkernel.dto.response.PagedResponse;
import com.example.userauthservice.dto.UserDTO;
import com.example.userauthservice.dto.response.AdminUserResponse;
import com.example.userauthservice.dto.response.UserLoginResponse;
import com.example.userauthservice.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {
    @Mapping(target = "createdAt", source = "createdDate")
    @Mapping(target = "lastUpdatedBy", source = "lastModifiedBy")
    @Mapping(target = "lastUpdatedAt", source = "lastModifiedDate")
    UserResponse toResponse(UserDTO userDTO);

    List<UserResponse> toResponse(List<UserDTO> userDTOS);

    @Mapping(target = "createdAt", source = "createdDate")
    @Mapping(target = "lastUpdatedBy", source = "lastModifiedBy")
    @Mapping(target = "lastUpdatedAt", source = "lastModifiedDate")
    AdminUserResponse toAdminResponse(UserDTO userDTO);

    List<AdminUserResponse> toAdminResponse(List<UserDTO> userDTOS);

    default PagedResponse<List<UserResponse>> toResponse(PageDTO<List<UserDTO>> records) {
        return PagedResponse.<List<UserResponse>>builder()
                .data(toResponse(records.getContent()))
                .totalElements(records.getTotalElements())
                .totalPages(records.getTotalPages())
                .pageNumber(records.getPageNumber())
                .pageSize(records.getPageSize())
                .sortBy(records.getSortBy())
                .sortDir(records.getSortDir())
                .build();
    }

    default PagedResponse<List<AdminUserResponse>> toAdminResponse(PageDTO<List<UserDTO>> records) {
        return PagedResponse.<List<AdminUserResponse>>builder()
                .data(toAdminResponse(records.getContent()))
                .totalElements(records.getTotalElements())
                .totalPages(records.getTotalPages())
                .pageNumber(records.getPageNumber())
                .pageSize(records.getPageSize())
                .sortBy(records.getSortBy())
                .sortDir(records.getSortDir())
                .build();
    }

    @Mapping(target = "roles", expression = "java(userDTO.getRoles() != null ? userDTO.getRoles().stream().map(role -> role.getName()).toList() : java.util.Collections.emptyList())")
    UserLoginResponse toLoginResponse(UserDTO userDTO);
}
