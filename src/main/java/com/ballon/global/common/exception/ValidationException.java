package com.ballon.global.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidationException extends BaseException {
    public ValidationException(List<ErrorResponse.FieldError> errors) {
        super("VALIDATION_ERROR", "입력 데이터에 오류가 있습니다.", HttpStatus.UNPROCESSABLE_ENTITY, errors);
    }
}

