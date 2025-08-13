package com.ballon.domain.user.exception;


import com.ballon.global.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("존재하지 않는 유저입니다.");
    }
}
