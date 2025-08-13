package com.ballon.global.common.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private String code;
    private String message;
    private int status;
    private String details;
    private Meta meta;

    public ErrorResponse(String code, String message, int status, String details) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.details = details;
        this.meta = new Meta();
    }

    public ErrorResponse(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.meta = new Meta();
    }
}
