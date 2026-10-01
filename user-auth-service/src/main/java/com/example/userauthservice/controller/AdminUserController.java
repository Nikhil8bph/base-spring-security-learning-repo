package com.example.userauthservice.controller;

import com.example.sharedkernel.constants.StatusCode;
import com.example.sharedkernel.dto.response.PagedResponse;
import com.example.sharedkernel.dto.response.StandardResponse;
import com.example.userauthservice.dto.response.AdminUserResponse;
import com.example.userauthservice.resources.AdminUserResources;
import com.example.userauthservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminUserController implements AdminUserResources {
    private final UserService userService;

    @Override
    public StandardResponse<PagedResponse<List<AdminUserResponse>>> searchAllUsers(String searchTerm,
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
                                                                                   String sortDir) {
        var users = userService.adminSearchAllUsers(searchTerm,
                searchType,
                minCreatedAt,
                maxCreatedAt,
                minLastUpdatedAt,
                maxLastUpdatedAt,
                minDeletedAt,
                maxDeletedAt,
                createdBy,
                lastUpdatedBy,
                deletedBy,
                active,
                deleted,
                pageSize,
                pageNumber,
                sortBy,
                sortDir);
        return StandardResponse
                .<PagedResponse<List<AdminUserResponse>>>builder()
                .data(users)
                .code(StatusCode.SUCCESS)
                .message("Users fetched successfully")
                .build();
    }
}
