package com.ballon.domain.user.exception;

import com.ballon.global.common.exception.ErrorResponse;
import com.ballon.global.common.exception.ValidationException;

import java.util.List;

public class InvalidSexValueException extends ValidationException {
    public InvalidSexValueException() {
        super(List.of(
                new ErrorResponse.FieldError("sex", "유효하지 않은 성별 값입니다.")
        ));
    }
}
