package com.example.userauthservice.facade;

import com.example.sharedkernel.dto.PageDTO;
import com.example.sharedkernel.exception.UserNotFoundException;
import com.example.userauthservice.dto.UserDTO;
import com.example.userauthservice.entity.User;
import com.example.userauthservice.enums.UserIdentifier;
import com.example.userauthservice.mapper.UserMapper;
import com.example.userauthservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO saveUser(UserDTO userDTO, String password) {
        var userEntity = userMapper.toEntity(userDTO);
        userEntity.setPassword(password);
        return userMapper.toDTO(
                userRepository.save(userEntity)
        );
    }

    public UserDTO updatePassword(Long userId, String password) {
        var userEntity = findUserById(userId);
        userEntity.setPassword(password);
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

    public boolean userExistsByEmailOrMobile(String email, String mobile) {
        try {
            findUserByEmailOrMobile(email, mobile);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    public PageDTO<List<UserDTO>> getAllUsers(Specification<User> userSpecification, Pageable pageable) {
        Page<User> users = userRepository.findAll(userSpecification, pageable);
        return userMapper.toDTO(users);
    }

    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        var userEntity = findUserById(userId);
        if (userDTO.getEmail() != null && !userDTO.getEmail().equalsIgnoreCase(userEntity.getEmail())
                && userRepository.existsByEmailAndIdNot(userDTO.getEmail(), userId)) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (userDTO.getMobile() != null && !userDTO.getMobile().equals(userEntity.getMobile())
                && userRepository.existsByMobileAndIdNot(userDTO.getMobile(), userId)) {
            throw new IllegalArgumentException("Mobile number is already in use");
        }
        if (userDTO.getUsername() != null) userEntity.setUsername(userDTO.getUsername());
        if (userDTO.getEmail() != null) userEntity.setEmail(userDTO.getEmail());
        if (userDTO.getMobile() != null) userEntity.setMobile(userDTO.getMobile());
        return userMapper.toDTO(userRepository.save(userEntity));
    }

    public void deleteUser(Long userId) {
        var userEntity = findUserById(userId);
        userEntity.delete("System"); //TODO : Get the logged in user
        userRepository.save(userEntity);
    }

    public UserDTO searchUserByUserName(String username) {
        var userEntity = findUserByUserName(username);
        return userMapper.toDTO(userEntity);
    }

    private User findUserByUserName(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found with username: %s".formatted(username)));
    }

    private User findUserByMobile(String mobile) {
        return userRepository.findByMobile(mobile).orElseThrow(() -> new UserNotFoundException("User not found with mobile: %s".formatted(mobile)));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found with email: %s".formatted(email)));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: %s".formatted(id)));
    }

    private User findUserByEmailOrMobile(String email, String mobile) {
        return userRepository.findByEmailOrMobile(email, mobile).orElseThrow(() -> new UserNotFoundException(
                "User not found with email or mobile: %s / %s".formatted(email, mobile)));
    }
}
