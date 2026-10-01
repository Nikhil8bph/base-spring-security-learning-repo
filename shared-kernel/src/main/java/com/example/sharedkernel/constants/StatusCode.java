package com.example.sharedkernel.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum StatusCode {
    SUCCESS(200, "Success"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    TOO_MANY_REQUESTS(429, "Too Many Requests");

    private final int code;
    private final String description;
}
