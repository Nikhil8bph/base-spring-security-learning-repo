package com.example.userauthservice.service.impl;

import com.example.sharedkernel.config.security.JwtUtils;
import com.example.sharedkernel.dto.PageDTO;
import com.example.sharedkernel.dto.response.PagedResponse;
import com.example.userauthservice.dto.UserDTO;
import com.example.userauthservice.dto.request.LoginRequest;
import com.example.userauthservice.dto.request.SignUpRequest;
import com.example.userauthservice.dto.request.UpdatePasswordRequest;
import com.example.userauthservice.dto.request.UserUpdateRequest;
import com.example.userauthservice.dto.response.AdminUserResponse;
import com.example.userauthservice.dto.response.UserLoginResponse;
import com.example.userauthservice.dto.response.UserResponse;
import com.example.userauthservice.entity.User;
import com.example.userauthservice.enums.UserIdentifier;
import com.example.userauthservice.facade.RoleFacade;
import com.example.userauthservice.facade.UserFacade;
import com.example.userauthservice.mapper.UserRequestMapper;
import com.example.userauthservice.mapper.UserResponseMapper;
import com.example.userauthservice.service.UserService;
import com.example.userauthservice.specifications.UserSearchSpecification;
import com.example.userauthservice.validator.SignUpRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.userauthservice.exception.ResourceConflictException;
import java.util.Locale;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserFacade userFacade;
    private final UserResponseMapper userResponseMapper;
    private final UserRequestMapper userRequestMapper;
    private final SignUpRequestValidator signUpRequestValidator;
    private final RoleFacade roleFacade;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public PagedResponse<List<UserResponse>> genericSearchAllUsers(String searchTerm,
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
        return userResponseMapper.toResponse(searchAllUsers(searchTerm,
                searchType,
                minCreatedAt,
                maxCreatedAt,
                minLastUpdatedAt,
                maxLastUpdatedAt,
                null,
                null,
                createdBy,
                lastUpdatedBy,
                null,
                active,
                false,
                pageSize,
                pageNumber,
                sortBy,
                sortDir));
    }

    @Override
    public PagedResponse<List<AdminUserResponse>> adminSearchAllUsers(String searchTerm,
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
        return userResponseMapper.toAdminResponse(searchAllUsers(searchTerm,
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
                sortDir));
    }

    @Override
    public UserResponse userSignUp(SignUpRequest signUpRequest) {
        signUpRequestValidator.validateSignUpRequest(signUpRequest);
        var userDTO = userFacade.userExistsByEmailOrMobile(signUpRequest.getEmail(), signUpRequest.getMobile());
        if (userDTO) {
            throw new ResourceConflictException("User already exists with this email or mobile number");
        } else {
            UserDTO user = userRequestMapper.toDTOFromSignUpRequest(signUpRequest);
            var defaultRoles = roleFacade.getDefaultRoles();
            if (defaultRoles.isEmpty()) {
                throw new IllegalStateException("No default roles found. Please configure default roles.");
            }
            user.setRoles(defaultRoles);
            UserDTO savedUser = userFacade.saveUser(user, signUpRequest.getPassword());
            return userResponseMapper.toResponse(savedUser);
        }
    }

    @Override
    public UserResponse getUser(String userVal, String userIdentifier) {
        var userIdentifierEnum = UserIdentifier.valueOf(userIdentifier);
        var user = userFacade.getUser(userVal, userIdentifierEnum);
        return userResponseMapper.toResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @userAuthorization.isOwner(#userId, authentication)")
    public UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        var userDTO = userRequestMapper.toDTOFromUserUpdateRequest(userUpdateRequest);
        var user = userFacade.updateUser(userId, userDTO);
        return userResponseMapper.toResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @userAuthorization.isOwner(#userId, authentication)")
    public void deleteUser(Long userId) {
        userFacade.deleteUser(userId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @userAuthorization.isOwner(#userId, authentication)")
    public UserResponse updatePassword(Long userId, UpdatePasswordRequest updatePasswordRequest) {
        var userDTO = userFacade.updatePassword(userId, updatePasswordRequest.getPassword());
        return userResponseMapper.toResponse(userDTO);
    }

    @Override
    public UserLoginResponse userLogIn(LoginRequest loginRequest) {
        UserIdentifier userIdentifier = UserIdentifier.valueOf(loginRequest.getSignUpUsing().toUpperCase(Locale.ROOT));
        String identifier = userIdentifier == UserIdentifier.EMAIL ? loginRequest.getEmail() : loginRequest.getMobile();
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("The selected login identifier is required");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, loginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        UserDTO userDTO = userFacade.getUser(identifier, userIdentifier);
        UserLoginResponse loginResponse = userResponseMapper.toLoginResponse(userDTO);

        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);

        Date accessExpiry = jwtUtils.extractExpiration(accessToken);
        Calendar accessCal = Calendar.getInstance();
        accessCal.setTime(accessExpiry);
        loginResponse.setExpirationDate(accessCal);

        Date refreshExpiry = jwtUtils.extractExpiration(refreshToken);
        Calendar refreshCal = Calendar.getInstance();
        refreshCal.setTime(refreshExpiry);
        loginResponse.setRefreshTokenExpirationDate(refreshCal);

        return loginResponse;
    }

    private PageDTO<List<UserDTO>> searchAllUsers(String searchTerm,
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
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        var searchTypeEnum = searchType == null || searchType.isBlank()
                ? null
                : UserIdentifier.valueOf(searchType);
        Specification<User> userSpec = UserSearchSpecification.getSpecification(searchTerm,
                searchTypeEnum,
                minCreatedAt,
                maxCreatedAt,
                minLastUpdatedAt,
                maxLastUpdatedAt,
                createdBy,
                lastUpdatedBy,
                minDeletedAt,
                maxDeletedAt,
                deletedBy,
                active,
                deleted);
        return userFacade.getAllUsers(userSpec, pageable);
    }
}
