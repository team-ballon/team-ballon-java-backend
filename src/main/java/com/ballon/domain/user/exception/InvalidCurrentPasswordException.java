package com.ballon.domain.user.exception;

import com.ballon.global.common.exception.ErrorResponse;
import com.ballon.global.common.exception.ValidationException;

import java.util.List;

public class InvalidCurrentPasswordException extends ValidationException {
    public InvalidCurrentPasswordException() {
        super(List.of(
                new ErrorResponse.FieldError("currentPassword", "입력하신 현재 비밀번호가 일치하지 않습니다.")
        ));
    }
}
