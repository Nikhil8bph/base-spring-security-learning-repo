package com.example.userauthservice.controller;

import com.example.sharedkernel.constants.StatusCode;
import com.example.sharedkernel.dto.response.PagedResponse;
import com.example.sharedkernel.dto.response.StandardResponse;
import com.example.userauthservice.dto.request.LoginRequest;
import com.example.userauthservice.dto.request.SignUpRequest;
import com.example.userauthservice.dto.request.UpdatePasswordRequest;
import com.example.userauthservice.dto.request.UserUpdateRequest;
import com.example.userauthservice.dto.response.UserLoginResponse;
import com.example.userauthservice.dto.response.UserResponse;
import com.example.userauthservice.resources.UserResources;
import com.example.userauthservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserResources {
    private final UserService userService;

    @Override
    public StandardResponse<PagedResponse<java.util.List<UserResponse>>> searchAllUsers(String searchTerm,
                                                                                        String searchType,
                                                                                        Calendar minCreatedAt,
                                                                                        Calendar maxCreatedAt,
                                                                                        Calendar minLastUpdatedAt,
                                                                                        Calendar maxLastUpdatedAt,
                                                                                        String createdBy,
                                                                                        String lastUpdatedBy,
                                                                                        boolean active,
                                                                                        int pageSize,
                                                                                        int pageNumber,
                                                                                        String sortBy,
                                                                                        String sortDir) {
        var users = userService.genericSearchAllUsers(searchTerm,
                searchType,
                minCreatedAt,
                maxCreatedAt,
                minLastUpdatedAt,
                maxLastUpdatedAt,
                createdBy,
                lastUpdatedBy,
                active,
                pageSize,
                pageNumber,
                sortBy,
                sortDir);
        return StandardResponse
                .<PagedResponse<java.util.List<UserResponse>>>builder()
                .data(users)
                .code(StatusCode.SUCCESS)
                .message("Users fetched successfully")
                .build();
    }

    @Override
    public StandardResponse<UserResponse> userSignUp(SignUpRequest signUpRequest) {
        var user = userService.userSignUp(signUpRequest);
        return StandardResponse
                .<UserResponse>builder()
                .data(user)
                .code(StatusCode.CREATED)
                .message("User created successfully")
                .build();
    }

    @Override
    public StandardResponse<UserLoginResponse> userLogIn(LoginRequest loginRequest) {
        UserLoginResponse user = userService.userLogIn(loginRequest);
        return StandardResponse
                .<UserLoginResponse>builder()
                .data(user)
                .code(StatusCode.SUCCESS)
                .message("User logged in successfully")
                .build();
    }

    @Override
    public StandardResponse<UserResponse> getUser(String userVal, String userIdentifier) {
        var user = userService.getUser(userVal, userIdentifier);
        return StandardResponse
                .<UserResponse>builder()
                .data(user)
                .code(StatusCode.SUCCESS)
                .message("User fetched successfully")
                .build();
    }

    @Override
    public StandardResponse<UserResponse> updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        var user = userService.updateUser(userId, userUpdateRequest);
        return StandardResponse
                .<UserResponse>builder()
                .data(user)
                .code(StatusCode.SUCCESS)
                .message("User updated successfully")
                .build();
    }

    @Override
    public StandardResponse<Void> deleteUser(Long userId) {
        userService.deleteUser(userId);
        return StandardResponse
                .<Void>builder()
                .code(StatusCode.NO_CONTENT)
                .message("User deleted successfully")
                .build();
    }

    @Override
    public StandardResponse<UserResponse> updatePassword(Long userId, UpdatePasswordRequest updatePasswordRequest) {
        var user = userService.updatePassword(userId, updatePasswordRequest);
        return StandardResponse
                .<UserResponse>builder()
                .data(user)
                .code(StatusCode.SUCCESS)
                .message("Password updated successfully")
                .build();
    }
}
