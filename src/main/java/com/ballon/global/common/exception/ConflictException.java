package com.ballon.global.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ConflictException extends BaseException {
    public ConflictException(List<ErrorResponse.FieldError> errors) {
        super(HttpStatus.CONFLICT.getReasonPhrase(), "중복된 데이터로 인해 충돌이 발생했습니다.", HttpStatus.CONFLICT, errors);
    }
}