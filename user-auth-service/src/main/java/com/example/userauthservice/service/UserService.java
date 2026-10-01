package com.example.userauthservice.service;

import com.example.sharedkernel.dto.response.PagedResponse;
import com.example.userauthservice.dto.request.LoginRequest;
import com.example.userauthservice.dto.request.SignUpRequest;
import com.example.userauthservice.dto.request.UpdatePasswordRequest;
import com.example.userauthservice.dto.request.UserUpdateRequest;
import com.example.userauthservice.dto.response.AdminUserResponse;
import com.example.userauthservice.dto.response.UserLoginResponse;
import com.example.userauthservice.dto.response.UserResponse;

import java.util.Calendar;

public interface UserService {

    PagedResponse<java.util.List<UserResponse>> genericSearchAllUsers(String searchTerm,
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
                                                                      String sortDir);

    PagedResponse<java.util.List<AdminUserResponse>> adminSearchAllUsers(String searchTerm,
                                                                         String searchType,
                                                                         Calendar minCreatedAt,
                                                                         Calendar maxCreatedAt,
                                                                         Calendar minLastUpdatedAt,
                                                                         Calendar maxLastUpdatedAt,
                                                                         Calendar minDeletedAt,
                                                                         Calendar maxDeletedAt,
                                                                         String createdBy,
                                                                         String lastUpdatedBy,
                                                                         String deletedBy,
                                                                         boolean active,
                                                                         boolean deleted,
                                                                         int pageSize,
                                                                         int pageNumber,
                                                                         String sortBy,
                                                                         String sortDir);

    UserResponse getUser(String userVal, String userIdentifier);

    UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest);

    void deleteUser(Long userId);

    UserResponse updatePassword(Long userId, UpdatePasswordRequest password);

    UserLoginResponse userLogIn(LoginRequest loginRequest);

    UserResponse userSignUp(SignUpRequest signUpRequest);
}
