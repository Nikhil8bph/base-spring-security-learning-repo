package com.example.userauthservice.security;

import com.example.userauthservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userAuthorization")
@RequiredArgsConstructor
public class UserAuthorization {
    private final UserRepository userRepository;

    public boolean isOwner(Long userId, Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && userRepository.existsByIdAndUsername(userId, authentication.getName());
    }
}
