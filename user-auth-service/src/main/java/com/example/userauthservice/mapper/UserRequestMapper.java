package com.example.userauthservice.mapper;

import com.example.userauthservice.dto.UserDTO;
import com.example.userauthservice.dto.request.SignUpRequest;
import com.example.userauthservice.dto.request.UserUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {

    UserDTO toDTOFromUserUpdateRequest(UserUpdateRequest userUpdateRequest);

    @Mapping(target = "username",
            expression = "java(signUpRequest.getEmail() != null && !signUpRequest.getEmail().isBlank() "
                    + "? signUpRequest.getEmail().split(\"@\")[0] "
                    + ": signUpRequest.getMobile())")
    UserDTO toDTOFromSignUpRequest(SignUpRequest signUpRequest);
}
