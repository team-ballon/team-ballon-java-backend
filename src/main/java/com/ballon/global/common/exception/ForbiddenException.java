package com.ballon.global.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ForbiddenException extends BaseException {
    public ForbiddenException(List<ErrorResponse.FieldError> errors)
    {
        super(HttpStatus.FORBIDDEN.getReasonPhrase(), "권한이 없습니다.", HttpStatus.FORBIDDEN, errors );
    }
}
