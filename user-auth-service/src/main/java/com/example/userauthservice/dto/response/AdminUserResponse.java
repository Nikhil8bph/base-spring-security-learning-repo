package com.example.userauthservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private String username;
    private String email;
    private String mobile;
    private Calendar createdAt;
    private Calendar lastUpdatedAt;
}
