package com.example.userauthservice.facade;

import com.example.sharedkernel.dto.PageDTO;
import com.example.sharedkernel.exception.UserNotFoundException;
import com.example.userauthservice.dto.UserDTO;
import com.example.userauthservice.entity.Role;
import com.example.userauthservice.entity.User;
import com.example.userauthservice.enums.UserIdentifier;
import com.example.userauthservice.exception.ResourceConflictException;
import com.example.userauthservice.exception.ResourceNotFoundException;
import com.example.userauthservice.mapper.UserMapper;
import com.example.userauthservice.repository.RoleRepository;
import com.example.userauthservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFacade {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public UserDTO saveUser(UserDTO userDTO, String password) {
        var userEntity = userMapper.toEntity(userDTO);
        Set<Role> roles = new HashSet<>();
        for (Role role : userEntity.getRoles()) {
            Long roleId = role.getId();
            if (roleId == null) {
                throw new IllegalArgumentException("Role ID is required to assign an existing role");
            }
            roles.add(roleRepository.findById(roleId).orElseThrow(
                    () -> new ResourceNotFoundException("Role not found with id: " + roleId)));
        }
        userEntity.setRoles(roles);
        userEntity.setPassword(passwordEncoder.encode(password));
        return userMapper.toDTO(
                userRepository.save(userEntity)
        );
    }

    @Transactional
    public void assignRoleToUserIfPresent(String username, String roleName) {
        userRepository.findByUsername(username).ifPresent(user -> {
            Role role = roleRepository.findByNameIgnoreCase(roleName).orElseThrow(
                    () -> new IllegalArgumentException("Role not found with name: " + roleName));
            user.getRoles().add(role);
        });
    }

    @Transactional
    public UserDTO updatePassword(Long userId, String password) {
        var userEntity = findUserById(userId);
        userEntity.setPassword(passwordEncoder.encode(password));
        return userMapper.toDTO(
                userRepository.save(userEntity)
        );
    }

    public UserDTO getUser(String userVal, UserIdentifier userIdentifier) {
        switch (userIdentifier) {
            case UserIdentifier.ID:
                return userMapper.toDTO(findUserById(Long.parseLong(userVal)));
            case UserIdentifier.EMAIL:
                return userMapper.toDTO(findUserByEmail(userVal));
            case UserIdentifier.USERNAME:
                return userMapper.toDTO(findUserByUserName(userVal));
            case UserIdentifier.MOBILE:
                return userMapper.toDTO(findUserByMobile(userVal));
        }
        throw new IllegalArgumentException("Invalid user identifier: " + userIdentifier + "");
    }

    public boolean userExistsByUsernameOrEmailOrMobile(String username, String email, String mobile) {
        return userRepository.existsByUsernameOrEmailOrMobile(username, email, mobile);
    }

    public boolean userExistsByEmailOrMobile(String email, String mobile) {
        return userRepository.existsByEmailOrMobile(email, mobile);
    }

    public PageDTO<List<UserDTO>> getAllUsers(Specification<User> userSpecification, Pageable pageable) {
        Page<User> users = userRepository.findAll(userSpecification, pageable);
        return userMapper.toDTO(users);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        var userEntity = findUserById(userId);
        if (userDTO.getEmail() != null && !userDTO.getEmail().equalsIgnoreCase(userEntity.getEmail())
                && userRepository.existsByEmailAndIdNot(userDTO.getEmail(), userId)) {
            throw new ResourceConflictException("Email is already in use");
        }
        if (userDTO.getMobile() != null && !userDTO.getMobile().equals(userEntity.getMobile())
                && userRepository.existsByMobileAndIdNot(userDTO.getMobile(), userId)) {
            throw new ResourceConflictException("Mobile number is already in use");
        }
        if (userDTO.getUsername() != null
                && userRepository.existsByUsernameAndIdNot(userDTO.getUsername(), userId)) {
            throw new ResourceConflictException("Username is already in use");
        }
        if (userDTO.getUsername() != null) userEntity.setUsername(userDTO.getUsername());
        if (userDTO.getEmail() != null) userEntity.setEmail(userDTO.getEmail());
        if (userDTO.getMobile() != null) userEntity.setMobile(userDTO.getMobile());
        return userMapper.toDTO(userRepository.save(userEntity));
    }

    @Transactional
    public void deleteUser(Long userId) {
        var userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + userId));
        if (Boolean.TRUE.equals(userEntity.getDeleted())) {
            return;
        }
        userEntity.delete("System"); //TODO : Get the logged in user
        userRepository.save(userEntity);
    }

    public UserDTO searchUserByUserName(String username) {
        var userEntity = findUserByUserName(username);
        return userMapper.toDTO(userEntity);
    }

    private User findUserByUserName(String username) {
        return userRepository.findByUsername(username).filter(user -> !Boolean.TRUE.equals(user.getDeleted()))
                .orElseThrow(() -> new UserNotFoundException("User not found with username: %s".formatted(username)));
    }

    private User findUserByMobile(String mobile) {
        return userRepository.findByMobile(mobile).filter(user -> !Boolean.TRUE.equals(user.getDeleted()))
                .orElseThrow(() -> new UserNotFoundException("User not found with mobile: %s".formatted(mobile)));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).filter(user -> !Boolean.TRUE.equals(user.getDeleted()))
                .orElseThrow(() -> new UserNotFoundException("User not found with email: %s".formatted(email)));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).filter(user -> !Boolean.TRUE.equals(user.getDeleted()))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: %s".formatted(id)));
    }
}
