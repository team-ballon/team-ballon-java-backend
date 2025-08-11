package com.ballon.domain.user.exception;


import com.ballon.global.common.exception.ErrorResponse;
import com.ballon.global.common.exception.NotFoundException;

import java.util.List;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super(List.of(
                new ErrorResponse.FieldError("email", "존재하지 않는 유저입니다.")
        ));
    }
}
