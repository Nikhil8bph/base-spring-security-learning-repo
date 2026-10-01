package com.example.sharedkernel.dto.response;

import com.example.sharedkernel.constants.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
@Builder
@Schema(name = "StandardResponse", description = "Standard API response envelope")
public class StandardResponse<T> {
    @Schema(description = "Response creation time", example = "2026-09-30T12:00:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private final String timestamp = Calendar.getInstance().getTime().toString();
    @Schema(description = "Response payload")
    private T data;
    @Schema(description = "Human-readable result message", example = "Request completed successfully")
    private String message;
    @Schema(description = "Application response code")
    private StatusCode code;
}
