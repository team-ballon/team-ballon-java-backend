package com.ballon.domain.user.exception;

import com.ballon.global.common.exception.ConflictException;
import com.ballon.global.common.exception.ErrorResponse;

import java.util.List;

public class UserConflictException extends ConflictException {
    public UserConflictException() {
        super(List.of(
                new ErrorResponse.FieldError("email", "이미 존재하는 유저 입니다.")
        ));
    }
}
