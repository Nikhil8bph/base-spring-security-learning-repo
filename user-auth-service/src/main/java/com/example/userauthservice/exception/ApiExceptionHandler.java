package com.example.userauthservice.exception;

import com.example.sharedkernel.constants.StatusCode;
import com.example.sharedkernel.dto.response.StandardResponse;
import com.example.sharedkernel.exception.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({ResourceNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<StandardResponse<Void>> notFound(RuntimeException ex) {
        return error(HttpStatus.NOT_FOUND, StatusCode.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({ResourceConflictException.class, DataIntegrityViolationException.class})
    public ResponseEntity<StandardResponse<Void>> conflict(RuntimeException ex) {
        return error(HttpStatus.CONFLICT, StatusCode.CONFLICT,
                ex instanceof ResourceConflictException ? ex.getMessage() : "A record with these unique values already exists");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<Void>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage()).sorted()
                .reduce((a, b) -> a + "; " + b).orElse("Invalid request");
        return error(HttpStatus.BAD_REQUEST, StatusCode.BAD_REQUEST, message);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<StandardResponse<Void>> badRequest(Exception ex) {
        return error(HttpStatus.BAD_REQUEST, StatusCode.BAD_REQUEST,
                ex instanceof IllegalArgumentException ? ex.getMessage() : "Invalid request value or JSON body");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardResponse<Void>> unauthorized(AuthenticationException ex) {
        return error(HttpStatus.UNAUTHORIZED, StatusCode.UNAUTHORIZED, "Invalid credentials or disabled account");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardResponse<Void>> forbidden(AccessDeniedException ex) {
        return error(HttpStatus.FORBIDDEN, StatusCode.FORBIDDEN, "Access denied");
    }

    private ResponseEntity<StandardResponse<Void>> error(HttpStatus status, StatusCode code, String message) {
        return ResponseEntity.status(status).body(StandardResponse.<Void>builder().code(code).message(message).build());
    }
}
