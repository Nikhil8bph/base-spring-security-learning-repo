package com.example.userauthservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.List;

@Getter
@Setter
public class UserLoginResponse {
    private String username;
    private String email;
    private String mobile;
    private List<String> roles;
    private Calendar expirationDate;
    private Calendar refreshTokenExpirationDate;
    private String accessToken;
    private String refreshToken;
}
