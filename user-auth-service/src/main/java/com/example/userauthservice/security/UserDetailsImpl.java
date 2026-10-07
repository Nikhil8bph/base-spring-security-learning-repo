package com.example.userauthservice.security;

import com.example.userauthservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final User user;

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> {
                    String roleName = role.getName();;
                    if (roleName != null && !roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName;
                    }
                    assert roleName != null;
                    return new SimpleGrantedAuthority(roleName);
                })
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    @NullMarked
    public String getUsername() {
        return user.getUsername();
    }


    @Override
    public boolean isEnabled() {
        return user.getActive();
    }
}
