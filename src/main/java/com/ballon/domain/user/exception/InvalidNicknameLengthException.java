package com.ballon.domain.user.exception;

import com.ballon.global.common.exception.ErrorResponse;
import com.ballon.global.common.exception.ValidationException;

import java.util.List;

public class InvalidNicknameLengthException extends ValidationException {
    public InvalidNicknameLengthException() {
        super(List.of(
                new ErrorResponse.FieldError("nickname", "닉네임은 10자 이상일 수 없습니다.")
        ));
    }
}