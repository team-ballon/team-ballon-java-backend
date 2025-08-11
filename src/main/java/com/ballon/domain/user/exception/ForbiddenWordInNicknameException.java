package com.ballon.domain.user.exception;

import com.ballon.global.common.exception.ErrorResponse;
import com.ballon.global.common.exception.ValidationException;

import java.util.List;

public class ForbiddenWordInNicknameException extends ValidationException {
    public ForbiddenWordInNicknameException() {
        super(List.of(
                new ErrorResponse.FieldError("nickname", "비속어는 닉네임에 포함될 수 없습니다." )
        ));
    }
}
