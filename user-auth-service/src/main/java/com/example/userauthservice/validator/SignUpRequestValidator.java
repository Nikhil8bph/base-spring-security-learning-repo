package com.example.userauthservice.validator;

import com.example.userauthservice.dto.request.SignUpRequest;
import org.springframework.stereotype.Component;

@Component
public class SignUpRequestValidator {
    public void validateSignUpRequest(SignUpRequest signUpRequest) {
        if (signUpRequest.getEmail() == null && signUpRequest.getMobile() == null) {
            throw new IllegalArgumentException("Either email or mobile number should be provided");
        }
    }
}
